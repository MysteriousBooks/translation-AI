package com.translation.app.domain.repository

import com.translation.app.domain.model.Order
import com.translation.app.domain.model.Wallet
import com.translation.app.domain.model.WalletRecord

interface IWalletRepository {
    suspend fun getBalance(): Result<Wallet>
    suspend fun getRecords(type: Int?, page: Int, size: Int): Result<Pair<List<WalletRecord>, Long>>
    suspend fun recharge(payType: Int, amount: java.math.BigDecimal): Result<Order>
    suspend fun getRechargeStatus(orderNo: String): Result<Order>
}