package com.translation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.translation.common.enums.ResultCode;
import com.translation.common.enums.TranslateStatus;
import com.translation.common.enums.WalletRecordType;
import com.translation.common.exception.BusinessException;
import com.translation.common.utils.RedisUtil;
import com.translation.dto.app.TranslateDTO;
import com.translation.entity.TranslateRecord;
import com.translation.entity.User;
import com.translation.mapper.TranslateRecordMapper;
import com.translation.mapper.UserMapper;
import com.translation.service.SysConfigService;
import com.translation.service.TokenStatisticsService;
import com.translation.service.TranslateService;
import com.translation.service.UserService;
import com.translation.vo.app.TranslateResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslateServiceImpl extends ServiceImpl<TranslateRecordMapper, TranslateRecord> implements TranslateService {

    private final TranslateRecordMapper translateRecordMapper;
    private final UserMapper userMapper;
    private final SysConfigService sysConfigService;
    private final TokenStatisticsService tokenStatisticsService;
    private final RedisUtil redisUtil;

    @Value("${translation.llm.api-url}")
    private String apiUrl;

    @Value("${translation.llm.api-key}")
    private String apiKey;

    @Value("${translation.llm.model}")
    private String model;

    @Override
    @Transactional
    public TranslateResultVO translate(Long userId, TranslateDTO dto) {
        /* 检查语言支持 */
        String supportedLanguages = sysConfigService.getConfigValue("supported_languages");
        if (supportedLanguages != null) {
            String[] langs = supportedLanguages.split(",");
            if (!CollUtil.contains(Arrays.asList(langs), dto.getSourceLang()) ||
                    !CollUtil.contains(Arrays.asList(langs), dto.getTargetLang())) {
                throw new BusinessException(ResultCode.UNSUPPORTED_LANGUAGE);
            }
        }

        /* 检查用户状态 */
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        /* 计算费用 */
        int charCount = dto.getSourceText().length();
        BigDecimal pricePerKchar = new BigDecimal(sysConfigService.getConfigValue("price_per_kchar"));
        BigDecimal minConsume = new BigDecimal(sysConfigService.getConfigValue("min_consume"));
        BigDecimal costAmount = new BigDecimal(charCount)
                .multiply(pricePerKchar)
                .divide(new BigDecimal(1000), 6, RoundingMode.HALF_UP);
        if (costAmount.compareTo(minConsume) < 0) {
            costAmount = minConsume;
        }

        /* 检查余额 */
        if (user.getBalance().compareTo(costAmount) < 0) {
            throw new BusinessException(ResultCode.BALANCE_NOT_ENOUGH);
        }

        /* 翻译限流检查 */
        String limitKey = "translate:limit:" + userId;
        Long count = redisUtil.increment(limitKey, 1, 60, TimeUnit.SECONDS);
        if (count != null && count > 10) {
            throw new BusinessException("翻译请求过于频繁，请稍后再试");
        }

        /* 创建翻译记录 */
        TranslateRecord record = new TranslateRecord();
        record.setUserId(userId);
        record.setSourceLang(dto.getSourceLang());
        record.setTargetLang(dto.getTargetLang());
        record.setSourceText(dto.getSourceText());
        record.setCharCount(charCount);
        record.setCostAmount(costAmount);
        record.setPricePerKchar(pricePerKchar);
        record.setStatus(TranslateStatus.TRANSLATING.getCode());
        translateRecordMapper.insert(record);

        /* 调用LLM翻译 */
        String translatedText = "";
        String errorMsg = null;
        int tokenCount = 0;
        boolean success = false;

        try {
            String llmResponse = callLlmApi(dto);
            translatedText = llmResponse;
            success = true;

            /* 扣除余额 */
            String description = "翻译: " + dto.getSourceLang() + "->" + dto.getTargetLang() + ", " + charCount + "字符";
            UserService userService = SpringUtil.getBean(UserService.class);
            userService.updateUserBalance(userId, costAmount, description, null,
                    WalletRecordType.CONSUME.getCode());

        } catch (Exception e) {
            log.error("翻译失败, userId: {}", userId, e);
            errorMsg = e.getMessage();
        }

        /* 更新翻译记录 */
        record.setTranslatedText(translatedText);
        record.setTokenCount(tokenCount);
        record.setStatus(success ? TranslateStatus.SUCCESS.getCode() : TranslateStatus.FAIL.getCode());
        record.setErrorMsg(errorMsg);
        translateRecordMapper.updateById(record);

        /* 记录统计 */
        tokenStatisticsService.recordStatistics(tokenCount, charCount, costAmount, success);

        return toVO(record);
    }

    @Override
    public Page<TranslateResultVO> getTranslateHistory(Long userId, int page, int size) {
        Page<TranslateRecord> pageParam = new Page<>(page, size);
        Page<TranslateRecord> result = lambdaQuery()
                .eq(TranslateRecord::getUserId, userId)
                .orderByDesc(TranslateRecord::getCreateTime)
                .page(pageParam);

        Page<TranslateResultVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toVO).collect(java.util.stream.Collectors.toList()));
        return voPage;
    }

    @Override
    public TranslateResultVO getTranslateDetail(Long userId, Long id) {
        TranslateRecord record = translateRecordMapper.selectById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("翻译记录不存在");
        }
        return toVO(record);
    }

    private String callLlmApi(TranslateDTO dto) {
        String prompt = String.format(
                "You are a professional translator. Translate the following text from %s to %s. " +
                        "Only output the translation result, nothing else.\n\n%s",
                dto.getSourceLang(), dto.getTargetLang(), dto.getSourceText()
        );

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.3);
        requestBody.put("messages", CollUtil.newArrayList(message));

        String response = HttpRequest.post(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(requestBody))
                .timeout(60000)
                .execute()
                .body();

        cn.hutool.json.JSONObject json = JSONUtil.parseObj(response);
        return json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getStr("content");
    }

    private TranslateResultVO toVO(TranslateRecord record) {
        TranslateResultVO vo = new TranslateResultVO();
        BeanUtil.copyProperties(record, vo);
        return vo;
    }
}