package com.translation.app.repository.impl

import com.translation.app.data.api.services.AuthApiService
import com.translation.app.data.dto.auth.*
import com.translation.app.data.local.PreferencesManager
import com.translation.app.domain.repository.IAuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
        private val authApi: AuthApiService,
        private val prefs: PreferencesManager
) : IAuthRepository {

    private suspend fun saveAuth(data: AuthResponse) {
        prefs.saveToken(data.token)
        prefs.saveUserId(data.userId)
        data.nickname?.let { prefs.saveNickname(it) }
        data.avatar?.let { prefs.saveAvatar(it) }
    }

    override suspend fun loginByEmail(email: String, password: String): Result<AuthResponse> {
        return try {
            val res = authApi.loginByEmail(LoginRequest(email, password))
            if (res.code == 200 && res.data != null) {
                saveAuth(res.data); Result.success(res.data)
            } else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, code: String, nickname: String?): Result<AuthResponse> {
        return try {
            val res = authApi.register(RegisterRequest(email, password, code, nickname))
            if (res.code == 200 && res.data != null) {
                saveAuth(res.data); Result.success(res.data)
            } else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginByWechat(code: String, nickname: String?, avatar: String?): Result<AuthResponse> {
        return try {
            val res = authApi.loginByWechat(SocialLoginRequest(code, nickname, avatar))
            if (res.code == 200 && res.data != null) {
                saveAuth(res.data); Result.success(res.data)
            } else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginByAlipay(code: String, nickname: String?, avatar: String?): Result<AuthResponse> {
        return try {
            val res = authApi.loginByAlipay(SocialLoginRequest(code, nickname, avatar))
            if (res.code == 200 && res.data != null) {
                saveAuth(res.data); Result.success(res.data)
            } else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(email: String, code: String, newPassword: String): Result<Unit> {
        return try {
            val res = authApi.forgotPassword(ForgotPasswordRequest(email, code, newPassword))
            if (res.code == 200) Result.success(Unit) else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendCode(email: String): Result<Unit> {
        return try {
            val res = authApi.sendCode(SendCodeRequest(email))
            if (res.code == 200) Result.success(Unit) else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        prefs.clearAuth()
    }
}
