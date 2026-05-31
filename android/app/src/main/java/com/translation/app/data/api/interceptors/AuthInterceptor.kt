package com.translation.app.data.api.interceptors

import com.translation.app.data.local.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
        private val preferencesManager: PreferencesManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { preferencesManager.getToken().first() }
        val request = if (token != null) {
            originalRequest.newBuilder().header("Authorization", "Bearer $token").build()
        } else originalRequest
        val response = chain.proceed(request)
        if (response.code == 200) {
            val body = response.peekBody(Long.MAX_VALUE)
            try {
                val json = org.json.JSONObject(body.string())
                if (json.optInt("code") == 1001) {
                    runBlocking { preferencesManager.clearAuth() }
                }
            } catch (_: Exception) {
            }
        }
        return response
    }
}