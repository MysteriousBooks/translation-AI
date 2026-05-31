package com.translation.common.constant;

public class CommonConstant {

    private CommonConstant() {
    }

    public static final String APP_TOKEN_PREFIX = "app:token:";
    public static final String APP_ACTIVE_TOKEN_PREFIX = "app:token:active:";
    public static final String ADMIN_TOKEN_PREFIX = "admin:token:";
    public static final String VERIFY_CODE_PREFIX = "verify:code:";
    public static final String TRANSLATE_LIMIT_PREFIX = "translate:limit:";

    public static final int VERIFY_CODE_EXPIRE_MINUTES = 5;
    public static final int DEFAULT_PAGE_SIZE = 10;

    public static final String ORDER_NO_PREFIX = "ORD";
    public static final String REFUND_NO_PREFIX = "REF";
}