package com.translation.app.domain.model

import java.math.BigDecimal

data class User(
        val id: Long, val email: String?, val nickname: String?, val avatar: String?,
        val phone: String?, val loginType: Int, val balance: BigDecimal,
        val totalConsume: BigDecimal, val createTime: String?
)