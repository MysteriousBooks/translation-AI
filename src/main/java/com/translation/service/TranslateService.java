package com.translation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.dto.app.TranslateDTO;
import com.translation.entity.TranslateRecord;
import com.translation.vo.app.TranslateResultVO;

import java.math.BigDecimal;

public interface TranslateService extends IService<TranslateRecord> {

    TranslateResultVO translate(Long userId, TranslateDTO dto);

    Page<TranslateResultVO> getTranslateHistory(Long userId, int page, int size);

    TranslateResultVO getTranslateDetail(Long userId, Long id);

    /**
     * 保存翻译结果并处理余额扣减和统计（事务方法，需通过代理调用）
     */
    TranslateResultVO saveTranslateResult(Long userId, TranslateDTO dto, int charCount,
                                          BigDecimal costAmount, BigDecimal pricePerKchar,
                                          String translatedText, int tokenCount, boolean success, String errorMsg);
}