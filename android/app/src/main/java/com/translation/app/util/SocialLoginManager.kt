package com.translation.app.util

import android.app.Activity
import android.content.Intent
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 社交登录管理器 - 封装微信和支付宝登录SDK调用
 *
 * 微信登录流程:
 * 1. 调用 wechatLogin() -> 调起微信授权
 * 2. 用户授权后 -> WXEntryActivity 收到回调
 * 3. 从回调中获取 code -> 调用后端 /api/app/auth/login/wechat
 *
 * 支付宝登录流程:
 * 1. 调用 alipayLogin() -> 跳转支付宝授权页
 * 2. 用户授权后 -> onActivityResult 收到回调
 * 3. 从回调中获取 authCode -> 调用后端 /api/app/auth/login/alipay
 */
@Singleton
class SocialLoginManager @Inject constructor(
        private val app: android.app.Application
) {
    private var wxApi: IWXAPI? = null
    private var wechatAppId: String = ""
    private var wechatCallback: SocialLoginCallback? = null
    private var alipayCallback: SocialLoginCallback? = null

    /** 在 Application.onCreate() 中调用，注册微信SDK */
    fun registerWechat(appId: String) {
        wechatAppId = appId
        wxApi = WXAPIFactory.createWXAPI(app, appId, true)
        wxApi?.registerApp(appId)
    }

    fun getWxApi(): IWXAPI? = wxApi
    fun getWechatCallback(): SocialLoginCallback? = wechatCallback
    fun getAlipayCallback(): SocialLoginCallback? = alipayCallback

    /** 微信登录 - 在Activity中调用 */
    fun wechatLogin(activity: Activity, callback: SocialLoginCallback) {
        this.wechatCallback = callback
        val api = wxApi
        if (api == null || !api.isWXAppInstalled) {
            callback.onError("请先安装微信")
            return
        }

        val req = SendAuth.Req().apply {
            scope = "snsapi_userinfo"
            state = "translation_login"
        }
        api.sendReq(req)
    }

    /** 处理微信登录回调 - 在WXEntryActivity中调用 */
    fun handleWechatCallback(code: String?, state: String?) {
        val callback = wechatCallback
        if (callback == null) return

        if (code != null && state == "translation_login") {
            callback.onSuccess(code, null, null)
        } else {
            callback.onError("微信登录取消或失败")
        }
        wechatCallback = null
    }

    /** 支付宝登录 - 跳转支付宝授权页 */
    fun alipayLogin(activity: Activity, callback: SocialLoginCallback) {
        this.alipayCallback = callback
        try {
            // 支付宝授权登录通过URL Scheme方式
            // 需要在AndroidManifest中配置scheme
            val authUrl = "alipays://platformapi/startapp?appId=20000067&authType=AUTH_ACCOUNT&scope=auth_userinfo"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(authUrl))
            activity.startActivityForResult(intent, ALIPAY_LOGIN_REQUEST_CODE)
        } catch (e: Exception) {
            callback.onError("请先安装支付宝")
            alipayCallback = null
        }
    }

    /** 处理支付宝登录回调 - 在onActivityResult中调用 */
    fun handleAlipayCallback(data: Intent?) {
        val callback = alipayCallback
        if (callback == null) return

        try {
            val authCode = data?.data?.getQueryParameter("auth_code")
            if (authCode != null) {
                callback.onSuccess(authCode, null, null)
            } else {
                callback.onError("支付宝登录取消或失败")
            }
        } catch (e: Exception) {
            callback.onError("支付宝登录回调解析失败")
        }
        alipayCallback = null
    }

    interface SocialLoginCallback {
        fun onSuccess(code: String, nickname: String?, avatar: String?)
        fun onError(message: String)
    }

    companion object {
        const val ALIPAY_LOGIN_REQUEST_CODE = 10001
    }
}
