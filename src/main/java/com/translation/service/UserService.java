package com.translation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.dto.app.*;
import com.translation.entity.User;
import com.translation.vo.app.LoginVO;
import com.translation.vo.app.UserInfoVO;

public interface UserService extends IService<User> {

    LoginVO register(RegisterDTO dto);

    LoginVO loginByEmail(EmailLoginDTO dto);

    LoginVO loginByWechat(SocialLoginDTO dto);

    LoginVO loginByAlipay(SocialLoginDTO dto);

    void forgotPassword(ForgotPasswordDTO dto);

    void changePassword(Long userId, ChangePasswordDTO dto);

    UserInfoVO getUserInfo(Long userId);

    void updateUserInfo(Long userId, UpdateUserDTO dto);

    void sendVerifyCode(String email);

    void sendVerifyCodeEmail(String email, String code);

    void updateUserBalance(Long userId, java.math.BigDecimal amount, String description, String orderNo, int type);
}