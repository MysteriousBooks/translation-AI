package com.translation.common.enums;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    UNAUTHORIZED(1001, "未登录或Token已过期"),
    TOKEN_INVALID(1002, "Token无效"),
    ACCOUNT_DISABLED(1003, "账号已被停用"),
    PASSWORD_ERROR(1004, "密码错误"),
    EMAIL_EXISTS(1005, "邮箱已注册"),
    EMAIL_NOT_EXISTS(1006, "邮箱未注册"),
    CODE_ERROR(1007, "验证码错误"),
    CODE_EXPIRED(1008, "验证码已过期"),

    BALANCE_NOT_ENOUGH(2001, "余额不足"),
    ORDER_NOT_FOUND(2002, "订单不存在"),
    ORDER_STATUS_ERROR(2003, "订单状态异常"),
    REFUND_APPLY_EXISTS(2004, "已提交退款申请"),
    TRANSLATE_FAILED(2005, "翻译服务异常"),
    UNSUPPORTED_LANGUAGE(2006, "不支持的语言"),
    USER_NOT_FOUND(2007, "用户不存在"),
    REFUND_NOT_FOUND(2008, "退款记录不存在"),

    PARAM_ERROR(3001, "参数错误"),
    PARAM_MISSING(3002, "缺少必要参数");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}