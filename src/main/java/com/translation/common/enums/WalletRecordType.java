package com.translation.common.enums;

import lombok.Getter;

@Getter
public enum WalletRecordType {

    RECHARGE(1, "充值"),
    CONSUME(2, "消耗"),
    REFUND(3, "退款");

    private final int code;
    private final String desc;

    WalletRecordType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static WalletRecordType fromCode(int code) {
        for (WalletRecordType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid WalletRecordType code: " + code);
    }
}