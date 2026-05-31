package com.translation.app

import android.app.Application
import com.translation.app.util.PaymentManager
import com.translation.app.util.SocialLoginManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var socialLoginManager: SocialLoginManager
    @Inject
    lateinit var paymentManager: PaymentManager

    // 微信AppId - 从BuildConfig或配置中获取
    companion object {
        const val WECHAT_APP_ID = "wx_your_app_id" // 替换为实际微信AppId
    }

    override fun onCreate() {
        super.onCreate()
        // 注册微信SDK
        socialLoginManager.registerWechat(WECHAT_APP_ID)
        paymentManager.registerWechat(WECHAT_APP_ID)
    }
}
