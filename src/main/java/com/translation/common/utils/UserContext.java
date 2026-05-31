package com.translation.common.utils;

import com.translation.common.enums.ResultCode;
import com.translation.common.exception.BusinessException;

/**
 * 用户上下文工具类，基于ThreadLocal存储当前登录用户ID
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> ADMIN_ID = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static Long getRequiredUserId() {
        Long userId = USER_ID.get();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    public static void setAdminId(Long adminId) {
        ADMIN_ID.set(adminId);
    }

    public static Long getAdminId() {
        return ADMIN_ID.get();
    }

    public static Long getRequiredAdminId() {
        Long adminId = ADMIN_ID.get();
        if (adminId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return adminId;
    }

    public static void clear() {
        USER_ID.remove();
        ADMIN_ID.remove();
    }
}