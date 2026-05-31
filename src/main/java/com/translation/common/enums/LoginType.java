package com.translation.common.enums;

import lombok.Getter;

@Getter
public enum LoginType {

    EMAIL(1, "邮箱"),
    WECHAT(2, "微信"),
    ALIPAY(3, "支付宝");

    private final int code;
    private final String desc;

    LoginType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static LoginType fromCode(int code) {
        for (LoginType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid LoginType code: " + code);
    }
}