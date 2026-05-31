package com.translation.common.enums;

import lombok.Getter;

@Getter
public enum TranslateStatus {

    TRANSLATING(0, "翻译中"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败");

    private final int code;
    private final String desc;

    TranslateStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TranslateStatus fromCode(int code) {
        for (TranslateStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid TranslateStatus code: " + code);
    }
}