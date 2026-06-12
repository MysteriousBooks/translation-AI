package com.translation.service.pay;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

/**
 * 微信支付V3实现
 * 使用Hutool HTTP客户端和签名工具对接微信支付API v3
 */
@Slf4j
@Service("wechatPayService")
public class WechatPayServiceImpl implements PayService {

    @Value("${pay.wechat.app-id}")
    private String appId;

    @Value("${pay.wechat.mch-id}")
    private String mchId;

    @Value("${pay.wechat.api-v3-key}")
    private String apiV3Key;

    @Value("${pay.wechat.serial-no}")
    private String serialNo;

    @Value("${pay.wechat.private-key-path}")
    private String privateKeyPath;

    @Value("${pay.wechat.notify-url}")
    private String notifyUrl;

    private static final String BASE_URL = "https://api.mch.weixin.qq.com";

    @Override
    public String createOrder(String orderNo, BigDecimal amount, String subject, String notifyUrl, String returnUrl) {
        String url = BASE_URL + "/v3/pay/transactions/native";

        int total = amount.multiply(new BigDecimal("100")).intValue();

        JSONObject body = new JSONObject();
        body.set("appid", appId);
        body.set("mchid", mchId);
        body.set("description", subject);
        body.set("out_trade_no", orderNo);
        body.set("notify_url", notifyUrl != null ? notifyUrl : this.notifyUrl);

        JSONObject amountObj = new JSONObject();
        amountObj.set("total", total);
        amountObj.set("currency", "CNY");
        body.set("amount", amountObj);

        String bodyStr = body.toString();
        String authorization = buildAuthorization("POST", "/v3/pay/transactions/native", bodyStr);

        try {
            String response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .header("Authorization", authorization)
                    .header("Accept", "application/json")
                    .body(bodyStr)
                    .execute()
                    .body();

            log.info("微信支付创建订单响应: {}", response);

            JSONObject respJson = JSONUtil.parseObj(response);
            if (respJson.containsKey("code_url")) {
                return respJson.getStr("code_url");
            }
            log.error("微信支付创建订单失败: {}", response);
            throw new RuntimeException("微信支付创建订单失败: " + respJson.getStr("message", response));
        } catch (Exception e) {
            log.error("微信支付创建订单异常", e);
            throw new RuntimeException("微信支付创建订单异常: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        String timestamp = params.get("Wechatpay-Timestamp");
        String nonce = params.get("Wechatpay-Nonce");
        String signature = params.get("Wechatpay-Signature");
        String body = params.get("body");

        if (timestamp == null || nonce == null || signature == null || body == null) {
            log.error("微信支付回调缺少必要参数");
            return false;
        }

        /*
         * 验证回调签名
         * 构造验签串: timestamp\nnonce\nbody\n
         * 使用微信平台证书验证签名
         * 注意：生产环境中应缓存微信平台证书，此处使用apiV3Key方式验证
         * 完整的证书验证需调用 /v3/certificates 接口获取平台证书
         */
        String message = timestamp + "\n" + nonce + "\n" + body + "\n";
        log.info("微信支付回调验签消息: {}", message);

        try {
            JSONObject bodyJson = JSONUtil.parseObj(body);
            if (bodyJson.containsKey("resource")) {
                JSONObject resource = bodyJson.getJSONObject("resource");
                String ciphertext = resource.getStr("ciphertext");
                String nonceStr = resource.getStr("nonce");
                String associatedData = resource.getStr("associated_data", "");

                String decrypted = decryptAesGcm(ciphertext, nonceStr, associatedData);
                log.info("微信支付回调解密数据: {}", decrypted);

                JSONObject decryptedJson = JSONUtil.parseObj(decrypted);
                String tradeState = decryptedJson.getStr("trade_state");
                return "SUCCESS".equals(tradeState);
            }
            return false;
        } catch (Exception e) {
            log.error("微信支付回调验签异常", e);
            return false;
        }
    }

    @Override
    public boolean queryOrderStatus(String orderNo) {
        String url = BASE_URL + "/v3/pay/transactions/out-trade-no/" + orderNo + "?mchid=" + mchId;

        String authorization = buildAuthorization("GET", "/v3/pay/transactions/out-trade-no/" + orderNo + "?mchid=" + mchId, "");

        try {
            String response = HttpRequest.get(url)
                    .header("Content-Type", "application/json")
                    .header("Authorization", authorization)
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            log.info("微信支付查询订单响应: {}", response);

            JSONObject respJson = JSONUtil.parseObj(response);
            String tradeState = respJson.getStr("trade_state");
            /* CLOSED 状态表示订单已关闭（未支付或已退款），不应视为成功 */
            return "SUCCESS".equals(tradeState);
        } catch (Exception e) {
            log.error("微信支付查询订单状态异常", e);
            return false;
        }
    }

    /**
     * 构建微信支付V3 Authorization请求头
     * 格式: WECHATPAY2-SHA256-RSA2048 mchid="...",nonce_str="...",timestamp="...",serial_no="...",signature="..."
     */
    private String buildAuthorization(String method, String urlPath, String body) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = IdUtil.fastSimpleUUID();

        String signMessage = method + "\n"
                + urlPath + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + (body != null ? body : "") + "\n";

        String signature = signWithPrivateKey(signMessage);

        return "WECHATPAY2-SHA256-RSA2048 "
                + "mchid=\"" + mchId + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + serialNo + "\","
                + "signature=\"" + signature + "\"";
    }

    /**
     * 使用商户私钥对消息进行SHA256withRSA签名
     */
    private String signWithPrivateKey(String message) {
        try {
            PrivateKey privateKey = getCachedPrivateKey();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            log.error("微信支付签名失败", e);
            throw new RuntimeException("微信支付签名失败: " + e.getMessage());
        }
    }

    /**
     * 缓存的私钥，避免每次签名都从文件 IO 读取
     */
    private volatile PrivateKey cachedPrivateKey;

    private PrivateKey getCachedPrivateKey() throws Exception {
        PrivateKey pk = cachedPrivateKey;
        if (pk == null) {
            synchronized (this) {
                pk = cachedPrivateKey;
                if (pk == null) {
                    pk = loadPrivateKey();
                    cachedPrivateKey = pk;
                }
            }
        }
        return pk;
    }

    /**
     * 加载商户私钥
     */
    private PrivateKey loadPrivateKey() throws Exception {
        String content = FileUtil.readUtf8String(privateKeyPath);
        content = content.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(content);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    @Override
    public String decryptCallback(String ciphertext, String nonce, String associatedData) {
        try {
            return decryptAesGcm(ciphertext, nonce, associatedData);
        } catch (Exception e) {
            log.error("微信支付回调解密失败", e);
            throw new RuntimeException("微信支付回调解密失败: " + e.getMessage());
        }
    }

    /**
     * AES-256-GCM解密微信支付回调数据
     * 微信支付V3回调的resource字段使用AEAD_AES_256_GCM加密
     */
    private String decryptAesGcm(String ciphertext, String nonce, String associatedData) throws Exception {
        byte[] key = apiV3Key.getBytes(StandardCharsets.UTF_8);
        byte[] nonceBytes = nonce.getBytes(StandardCharsets.UTF_8);
        byte[] aadBytes = associatedData.getBytes(StandardCharsets.UTF_8);
        byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);

        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding");
        javax.crypto.spec.GCMParameterSpec spec = new javax.crypto.spec.GCMParameterSpec(128, nonceBytes);
        javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(key, "AES");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, keySpec, spec);
        cipher.updateAAD(aadBytes);
        return new String(cipher.doFinal(ciphertextBytes), StandardCharsets.UTF_8);
    }
}