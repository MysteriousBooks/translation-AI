package com.translation.app.data.dto.wallet

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class WalletResponse(val balance: BigDecimal = BigDecimal.ZERO, val totalConsume: BigDecimal = BigDecimal.ZERO)

@Serializable
data class RechargeRequest(val payType: Int, val amount: BigDecimal)

@Serializable
data class OrderResponse(
        val id: Long, val orderNo: String, val payType: Int,
        val amount: BigDecimal, val status: Int = 0,
        val payTime: String? = null, val createTime: String? = null,
        val payData: String? = null
)

@Serializable
data class WalletRecordResponse(
        val id: Long, val type: Int, val amount: BigDecimal,
        val balanceBefore: BigDecimal = BigDecimal.ZERO, val balanceAfter: BigDecimal = BigDecimal.ZERO,
        val description: String? = null, val createTime: String? = null
)
