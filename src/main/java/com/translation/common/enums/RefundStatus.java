package com.translation.common.enums;

import lombok.Getter;

@Getter
public enum RefundStatus {

    PENDING(0, "待审核"),
    APPROVED(1, "已同意"),
    REJECTED(2, "已拒绝"),
    REFUNDING(3, "退款中"),
    COMPLETED(4, "已完成");

    private final int code;
    private final String desc;

    RefundStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RefundStatus fromCode(int code) {
        for (RefundStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid RefundStatus code: " + code);
    }
}