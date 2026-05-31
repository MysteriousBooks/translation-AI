package com.translation.controller.app;

import com.translation.common.result.Result;
import com.translation.dto.app.*;
import com.translation.service.UserService;
import com.translation.vo.app.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "APP端-认证")
@RestController
@RequestMapping("/api/app/auth")
@RequiredArgsConstructor
public class AppAuthController {

    private final UserService userService;

    @ApiOperation("邮箱注册")
    @PostMapping("/register")
    public Result<LoginVO> register(@Validated @RequestBody RegisterDTO dto) {
        return Result.success(userService.register(dto));
    }

    @ApiOperation("邮箱登录")
    @PostMapping("/login/email")
    public Result<LoginVO> loginByEmail(@Validated @RequestBody EmailLoginDTO dto) {
        return Result.success(userService.loginByEmail(dto));
    }

    @ApiOperation("微信登录")
    @PostMapping("/login/wechat")
    public Result<LoginVO> loginByWechat(@Validated @RequestBody SocialLoginDTO dto) {
        return Result.success(userService.loginByWechat(dto));
    }

    @ApiOperation("支付宝登录")
    @PostMapping("/login/alipay")
    public Result<LoginVO> loginByAlipay(@Validated @RequestBody SocialLoginDTO dto) {
        return Result.success(userService.loginByAlipay(dto));
    }

    @ApiOperation("忘记密码")
    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@Validated @RequestBody ForgotPasswordDTO dto) {
        userService.forgotPassword(dto);
        return Result.success();
    }

    @ApiOperation("发送验证码")
    @PostMapping("/send-code")
    public Result<Void> sendCode(@Validated @RequestBody SendCodeDTO dto) {
        userService.sendVerifyCode(dto.getEmail());
        return Result.success();
    }
}