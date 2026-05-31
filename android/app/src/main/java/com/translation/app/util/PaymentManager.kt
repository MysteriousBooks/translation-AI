package com.translation.app.util

import android.app.Activity
import com.alipay.sdk.app.PayTask
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 支付管理器 - 封装支付宝和微信支付SDK调用
 *
 * 使用前需在 AndroidManifest 中配置:
 * - 微信: wxapi/WXPayEntryActivity (已在此文件下方声明)
 * - 支付宝: 无需额外配置
 *
 * 需在 Application.onCreate() 中注册微信SDK:
 * - PaymentManager.registerWechat(appId)
 */
@Singleton
class PaymentManager @Inject constructor(
        private val app: android.app.Application
) {
    private var wxApi: IWXAPI? = null
    private var wechatAppId: String = ""

    /** 在 Application.onCreate() 中调用，注册微信SDK */
    fun registerWechat(appId: String) {
        wechatAppId = appId
        wxApi = WXAPIFactory.createWXAPI(app, appId, true)
        wxApi?.registerApp(appId)
    }

    fun getWxApi(): IWXAPI? = wxApi

    /** 支付宝支付 - 在Activity中调用 */
    fun payAlipay(activity: Activity, payData: String, callback: PaymentCallback) {
        try {
            val payTask = PayTask(activity)
            val result = payTask.payV2(payData, true)
            val resultStatus = result["resultStatus"]
            if (resultStatus == "9000") {
                callback.onResult(true, null)
            } else {
                callback.onResult(false, result["memo"] ?: "支付宝支付失败($resultStatus)")
            }
        } catch (e: Exception) {
            callback.onResult(false, e.message ?: "支付宝支付异常")
        }
    }

    /** 微信支付 - 解析payData中的参数后调用 */
    fun payWechat(prepayId: String, nonceStr: String, timeStamp: String, sign: String, callback: PaymentCallback) {
        val api = wxApi
        if (api == null || !api.isWXAppInstalled) {
            callback.onResult(false, "请先安装微信")
            return
        }

        try {
            val request = PayReq().apply {
                appId = wechatAppId
                partnerId = "" // 商户号，从payData解析或配置
                prepayId = prepayId
                packageValue = "Sign=WXPay"
                nonceStr = nonceStr
                timeStamp = timeStamp
                sign = sign
            }
            val result = api.sendReq(request)
            if (!result) {
                callback.onResult(false, "微信支付调起失败")
            }
            // 支付结果通过 WXPayEntryActivity 回调处理
        } catch (e: Exception) {
            callback.onResult(false, e.message ?: "微信支付异常")
        }
    }

    /** 从后端返回的payData字符串中解析微信支付参数并调用 */
    fun payWechatFromPayData(payDataStr: String, callback: PaymentCallback) {
        try {
            // payData格式: prepayId=xxx&nonceStr=xxx&timeStamp=xxx&sign=xxx&partnerId=xxx
            val params = payDataStr.split("&").associate {
                val (key, value) = it.split("=", limit = 2)
                key to value
            }
            payWechat(
                    prepayId = params["prepayId"] ?: "",
                    nonceStr = params["nonceStr"] ?: "",
                    timeStamp = params["timeStamp"] ?: "",
                    sign = params["sign"] ?: "",
                    callback = callback
            )
        } catch (e: Exception) {
            callback.onResult(false, "支付参数解析失败")
        }
    }

    interface PaymentCallback {
        fun onResult(success: Boolean, message: String?)
    }
}
