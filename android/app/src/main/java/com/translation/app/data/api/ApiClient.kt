package com.translation.app.data.api

import com.translation.app.data.api.interceptors.AuthInterceptor
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(
        private val authInterceptor: AuthInterceptor
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                .connectTimeout(ApiConfig.CONNECT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(ApiConfig.READ_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.WRITE_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(ApiConfig.baseUrl)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
    }

    inline fun <reified T> createService(): T = retrofit.create(T::class.java)
}