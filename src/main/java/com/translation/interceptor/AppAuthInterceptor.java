package com.translation.interceptor;

import cn.hutool.core.util.StrUtil;
import com.translation.common.constant.CommonConstant;
import com.translation.common.enums.ResultCode;
import com.translation.common.exception.BusinessException;
import com.translation.common.utils.JwtUtil;
import com.translation.common.utils.RedisUtil;
import com.translation.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // CORS预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        token = token.replace("Bearer ", "");

        /* 校验Token是否在黑名单中 */
        String blackKey = CommonConstant.APP_TOKEN_PREFIX + "black:" + token;
        if (Boolean.TRUE.equals(redisUtil.hasKey(blackKey))) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        if (jwtUtil.isTokenExpired(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String type = jwtUtil.getTokenType(token);
        if (!"app".equals(type)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        Long userId = jwtUtil.getUserId(token);

        /* 单端登录校验：Token必须与Redis中的活跃Token一致 */
        String activeKey = CommonConstant.APP_ACTIVE_TOKEN_PREFIX + userId;
        String activeToken = redisUtil.get(activeKey);
        if (activeToken == null || !activeToken.equals(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        UserContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}