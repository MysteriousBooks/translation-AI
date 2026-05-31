package com.translation.app.data.dto.user

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class UpdateUserRequest(val nickname: String? = null, val avatar: String? = null, val phone: String? = null)

@Serializable
data class ChangePasswordRequest(val oldPassword: String, val newPassword: String)

@Serializable
data class UserInfoResponse(
        val id: Long, val email: String? = null, val nickname: String? = null,
        val avatar: String? = null, val phone: String? = null,
        val loginType: Int = 1, val balance: BigDecimal = BigDecimal.ZERO,
        val totalConsume: BigDecimal = BigDecimal.ZERO, val createTime: String? = null
)
