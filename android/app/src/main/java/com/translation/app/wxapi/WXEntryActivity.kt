package com.translation.app.wxapi

import android.os.Bundle
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.translation.app.App
import com.translation.app.util.SocialLoginManager

/** 微信登录回调Activity - 包名必须为 wxapi.WXEntryActivity */
class WXEntryActivity : android.app.Activity(), IWXAPIEventHandler {

    private val socialLoginManager: SocialLoginManager by lazy {
        (application as App).socialLoginManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val api = socialLoginManager.getWxApi()
        api?.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val api = socialLoginManager.getWxApi()
        api?.handleIntent(intent, this)
    }

    override fun onReq(req: com.tencent.mm.opensdk.modelbase.BaseReq) {
        finish()
    }

    override fun onResp(resp: com.tencent.mm.opensdk.modelbase.BaseResp) {
        if (resp.type == ConstantsAPI.COMMAND_SENDAUTH) {
            val authResp = resp as SendAuth.Resp
            socialLoginManager.handleWechatCallback(authResp.code, authResp.state)
        }
        finish()
    }
}
