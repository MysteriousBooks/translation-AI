package com.translation.common.enums;

import lombok.Getter;

@Getter
public enum PayType {

    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信");

    private final int code;
    private final String desc;

    PayType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PayType fromCode(int code) {
        for (PayType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid PayType code: " + code);
    }
}