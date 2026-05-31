package com.translation.app.data.api.services

import com.translation.app.data.dto.ApiResponse
import com.translation.app.data.dto.auth.*
import retrofit2.http.*

interface AuthApiService {
    @POST("/api/app/auth/login/email")
    suspend fun loginByEmail(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("/api/app/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @POST("/api/app/auth/login/wechat")
    suspend fun loginByWechat(@Body request: SocialLoginRequest): ApiResponse<AuthResponse>

    @POST("/api/app/auth/login/alipay")
    suspend fun loginByAlipay(@Body request: SocialLoginRequest): ApiResponse<AuthResponse>

    @POST("/api/app/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ApiResponse<Unit>

    @POST("/api/app/auth/send-code")
    suspend fun sendCode(@Body request: SendCodeRequest): ApiResponse<Unit>
}