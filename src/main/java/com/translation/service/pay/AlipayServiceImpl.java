package com.translation.service.pay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.translation.common.enums.ResultCode;
import com.translation.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝支付实现
 * 注意：实际项目中需要根据具体支付方式(PagePay/WapPay/AppPay)调整
 */
@Slf4j
@Service("alipayService")
public class AlipayServiceImpl implements PayService {

    @Value("${pay.alipay.app-id}")
    private String appId;

    @Value("${pay.alipay.private-key}")
    private String privateKey;

    @Value("${pay.alipay.public-key}")
    private String alipayPublicKey;

    @Value("${pay.alipay.notify-url}")
    private String notifyUrl;

    @Value("${pay.alipay.return-url}")
    private String returnUrl;

    @Value("${pay.alipay.sandbox}")
    private boolean sandbox;

    private static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    private static final String SANDBOX_GATEWAY_URL = "https://openapi.alipaydev.com/gateway.do";

    private AlipayClient getAlipayClient() {
        String gatewayUrl = sandbox ? SANDBOX_GATEWAY_URL : GATEWAY_URL;
        return new DefaultAlipayClient(gatewayUrl, appId, privateKey, "json", "UTF-8", alipayPublicKey, "RSA2");
    }

    @Override
    public String createOrder(String orderNo, BigDecimal amount, String subject, String notifyUrl, String returnUrl) {
        AlipayClient client = getAlipayClient();
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl != null ? notifyUrl : this.notifyUrl);
        request.setReturnUrl(returnUrl != null ? returnUrl : this.returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderNo);
        bizContent.put("total_amount", amount.toPlainString());
        bizContent.put("subject", subject);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toJSONString());

        try {
            return client.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            log.error("支付宝创建订单失败", e);
            throw new BusinessException(ResultCode.FAIL.getCode(), "支付宝创建订单失败");
        }
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2");
        } catch (AlipayApiException e) {
            log.error("支付宝回调签名验证失败", e);
            return false;
        }
    }

    @Override
    public boolean queryOrderStatus(String orderNo) {
        AlipayClient client = getAlipayClient();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderNo);
        request.setBizContent(bizContent.toJSONString());

        try {
            AlipayTradeQueryResponse response = client.execute(request);
            if (response.isSuccess()) {
                String tradeStatus = response.getTradeStatus();
                return "TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus);
            }
            return false;
        } catch (AlipayApiException e) {
            log.error("支付宝查询订单状态失败", e);
            return false;
        }
    }
}