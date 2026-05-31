package com.translation.app.domain.repository

import com.translation.app.domain.model.User
import com.translation.app.data.dto.auth.AuthResponse

interface IAuthRepository {
    suspend fun loginByEmail(email: String, password: String): Result<AuthResponse>
    suspend fun register(email: String, password: String, code: String, nickname: String?): Result<AuthResponse>
    suspend fun loginByWechat(code: String, nickname: String?, avatar: String?): Result<AuthResponse>
    suspend fun loginByAlipay(code: String, nickname: String?, avatar: String?): Result<AuthResponse>
    suspend fun forgotPassword(email: String, code: String, newPassword: String): Result<Unit>
    suspend fun sendCode(email: String): Result<Unit>
    suspend fun logout()
}