package com.translation.app.data.api

import com.translation.app.BuildConfig

object ApiConfig {
    val baseUrl: String
        get() = BuildConfig.BASE_URL

    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}
