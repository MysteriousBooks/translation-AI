package com.translation.service.pay;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付服务接口
 */
public interface PayService {

    /**
     * 创建支付订单，返回支付参数/链接
     */
    String createOrder(String orderNo, BigDecimal amount, String subject, String notifyUrl, String returnUrl);

    /**
     * 验证回调签名并解析参数
     */
    boolean verifyCallback(Map<String, String> params);

    /**
     * 查询订单支付状态
     */
    boolean queryOrderStatus(String orderNo);
}