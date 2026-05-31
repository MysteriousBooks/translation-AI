package com.translation.app.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val email: String, val password: String, val code: String, val nickname: String? = null)

@Serializable
data class SocialLoginRequest(val code: String, val nickname: String? = null, val avatar: String? = null)

@Serializable
data class ForgotPasswordRequest(val email: String, val code: String, val newPassword: String)

@Serializable
data class SendCodeRequest(val email: String)

@Serializable
data class AuthResponse(val token: String, val userId: Long, val nickname: String? = null, val avatar: String? = null)
