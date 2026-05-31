package com.translation.app.wxapi

import android.os.Bundle
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.translation.app.App
import com.translation.app.util.PaymentManager

/** 微信支付回调Activity - 包名必须为 wxapi.WXPayEntryActivity */
class WXPayEntryActivity : android.app.Activity(), IWXAPIEventHandler {

    private val paymentManager: PaymentManager by lazy {
        (application as App).paymentManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val api = paymentManager.getWxApi()
        api?.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val api = paymentManager.getWxApi()
        api?.handleIntent(intent, this)
    }

    override fun onReq(req: com.tencent.mm.opensdk.modelbase.BaseReq) {
        finish()
    }

    override fun onResp(resp: com.tencent.mm.opensdk.modelbase.BaseResp) {
        // 支付结果通过EventBus或LiveData通知RechargeResultScreen
        // 0=成功, -1=错误, -2=用户取消
        finish()
    }
}
