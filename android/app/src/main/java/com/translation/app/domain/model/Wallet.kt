package com.translation.app.domain.model

import java.math.BigDecimal

data class Wallet(val balance: BigDecimal, val totalConsume: BigDecimal)

data class WalletRecord(
        val id: Long, val type: Int, val amount: BigDecimal,
        val balanceBefore: BigDecimal, val balanceAfter: BigDecimal,
        val description: String?, val createTime: String?
)