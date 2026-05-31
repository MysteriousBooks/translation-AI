package com.translation.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.translation.common.constant.CommonConstant;
import com.translation.common.enums.LoginType;
import com.translation.common.enums.ResultCode;
import com.translation.common.enums.WalletRecordType;
import com.translation.common.exception.BusinessException;
import com.translation.common.utils.JwtUtil;
import com.translation.common.utils.RedisUtil;
import com.translation.dto.app.*;
import com.translation.entity.User;
import com.translation.entity.WalletRecord;
import com.translation.mapper.UserMapper;
import com.translation.mapper.WalletRecordMapper;
import com.translation.service.UserService;
import com.translation.vo.app.LoginVO;
import com.translation.vo.app.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final WalletRecordMapper walletRecordMapper;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.code-expire-minutes}")
    private int codeExpireMinutes;

    /**
     * 存储活跃Token到Redis，实现单端登录
     * 同一用户新登录后，旧Token自动失效
     */
    private void storeActiveToken(Long userId, String token) {
        String activeKey = CommonConstant.APP_ACTIVE_TOKEN_PREFIX + userId;
        long expiration = jwtUtil.getAppExpirationSeconds();
        redisUtil.set(activeKey, token, expiration, TimeUnit.SECONDS);
    }

    /**
     * 构建登录响应
     */
    private LoginVO buildLoginVO(Long userId, String token, User user) {
        storeActiveToken(userId, token);
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(userId);
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        return vo;
    }

    @Override
    @Transactional
    public LoginVO register(RegisterDTO dto) {
        User existUser = lambdaQuery().eq(User::getEmail, dto.getEmail()).one();
        if (existUser != null) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }

        verifyCode(dto.getEmail(), dto.getCode());

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        user.setNickname(StrUtil.isNotBlank(dto.getNickname()) ? dto.getNickname() : dto.getEmail().split("@")[0]);
        user.setLoginType(LoginType.EMAIL.getCode());
        user.setStatus(1);
        user.setBalance(BigDecimal.ZERO);
        user.setTotalConsume(BigDecimal.ZERO);
        userMapper.insert(user);

        String token = jwtUtil.generateAppToken(user.getId());
        return buildLoginVO(user.getId(), token, user);
    }

    @Override
    public LoginVO loginByEmail(EmailLoginDTO dto) {
        User user = lambdaQuery().eq(User::getEmail, dto.getEmail()).one();
        if (user == null) {
            throw new BusinessException(ResultCode.EMAIL_NOT_EXISTS);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        String token = jwtUtil.generateAppToken(user.getId());
        return buildLoginVO(user.getId(), token, user);
    }

    @Override
    @Transactional
    public LoginVO loginByWechat(SocialLoginDTO dto) {
        String openid = dto.getCode();

        User user = lambdaQuery().eq(User::getWechatOpenid, openid).one();
        if (user == null) {
            user = new User();
            user.setWechatOpenid(openid);
            user.setNickname(StrUtil.isNotBlank(dto.getNickname()) ? dto.getNickname() : "微信用户");
            user.setAvatar(dto.getAvatar());
            user.setLoginType(LoginType.WECHAT.getCode());
            user.setStatus(1);
            user.setBalance(BigDecimal.ZERO);
            user.setTotalConsume(BigDecimal.ZERO);
            userMapper.insert(user);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        String token = jwtUtil.generateAppToken(user.getId());
        return buildLoginVO(user.getId(), token, user);
    }

    @Override
    @Transactional
    public LoginVO loginByAlipay(SocialLoginDTO dto) {
        String alipayUserId = dto.getCode();

        User user = lambdaQuery().eq(User::getAlipayUserId, alipayUserId).one();
        if (user == null) {
            user = new User();
            user.setAlipayUserId(alipayUserId);
            user.setNickname(StrUtil.isNotBlank(dto.getNickname()) ? dto.getNickname() : "支付宝用户");
            user.setAvatar(dto.getAvatar());
            user.setLoginType(LoginType.ALIPAY.getCode());
            user.setStatus(1);
            user.setBalance(BigDecimal.ZERO);
            user.setTotalConsume(BigDecimal.ZERO);
            userMapper.insert(user);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        String token = jwtUtil.generateAppToken(user.getId());
        return buildLoginVO(user.getId(), token, user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordDTO dto) {
        User user = lambdaQuery().eq(User::getEmail, dto.getEmail()).one();
        if (user == null) {
            throw new BusinessException(ResultCode.EMAIL_NOT_EXISTS);
        }

        verifyCode(dto.getEmail(), dto.getCode());

        user.setPassword(BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt()));
        userMapper.updateById(user);

        /* 重置密码后踢下线 */
        redisUtil.delete(CommonConstant.APP_ACTIVE_TOKEN_PREFIX + user.getId());
        redisUtil.delete(CommonConstant.VERIFY_CODE_PREFIX + dto.getEmail());
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        user.setPassword(BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt()));
        userMapper.updateById(user);

        /* 修改密码后踢下线，要求重新登录 */
        redisUtil.delete(CommonConstant.APP_ACTIVE_TOKEN_PREFIX + userId);
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        UserInfoVO vo = new UserInfoVO();
        cn.hutool.core.bean.BeanUtil.copyProperties(user, vo);
        return vo;
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, UpdateUserDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (StrUtil.isNotBlank(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StrUtil.isNotBlank(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }
        if (StrUtil.isNotBlank(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        userMapper.updateById(user);
    }

    @Override
    public void sendVerifyCode(String email) {
        String rateKey = CommonConstant.VERIFY_CODE_PREFIX + "rate:" + email;
        if (Boolean.TRUE.equals(redisUtil.hasKey(rateKey))) {
            throw new BusinessException("发送验证码过于频繁，请稍后再试");
        }

        String code = RandomUtil.randomNumbers(6);

        String codeKey = CommonConstant.VERIFY_CODE_PREFIX + email;
        redisUtil.set(codeKey, code, codeExpireMinutes, TimeUnit.MINUTES);
        redisUtil.set(rateKey, "1", 60, TimeUnit.SECONDS);

        sendVerifyCodeEmail(email, code);
    }

    @Async
    public void sendVerifyCodeEmail(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("AI翻译服务 - 验证码");
            message.setText("您的验证码为：" + code + "，有效期" + codeExpireMinutes + "分钟，请勿泄露。");
            mailSender.send(message);
            log.info("验证码邮件发送成功: {}", email);
        } catch (Exception e) {
            log.error("验证码邮件发送失败: {}", email, e);
        }
    }

    @Override
    @Transactional
    public void updateUserBalance(Long userId, BigDecimal amount, String description, String orderNo, int type) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        BigDecimal balanceBefore = user.getBalance();
        BigDecimal balanceAfter;

        if (type == WalletRecordType.CONSUME.getCode()) {
            balanceAfter = balanceBefore.subtract(amount);
            if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(ResultCode.BALANCE_NOT_ENOUGH);
            }
            user.setTotalConsume(user.getTotalConsume().add(amount));
        } else {
            balanceAfter = balanceBefore.add(amount);
        }

        user.setBalance(balanceAfter);
        userMapper.updateById(user);

        WalletRecord record = new WalletRecord();
        record.setUserId(userId);
        record.setType(type);
        record.setAmount(amount);
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setRelatedOrderNo(orderNo);
        record.setDescription(description);
        walletRecordMapper.insert(record);
    }

    private void verifyCode(String email, String code) {
        String codeKey = CommonConstant.VERIFY_CODE_PREFIX + email;
        String cachedCode = redisUtil.get(codeKey);
        if (cachedCode == null) {
            throw new BusinessException(ResultCode.CODE_EXPIRED);
        }
        if (!cachedCode.equals(code)) {
            throw new BusinessException(ResultCode.CODE_ERROR);
        }
    }
}