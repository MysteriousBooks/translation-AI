package com.translation.app.repository.impl

import com.translation.app.data.api.services.WalletApiService
import com.translation.app.data.dto.wallet.RechargeRequest
import com.translation.app.domain.model.Order
import com.translation.app.domain.model.Wallet
import com.translation.app.domain.model.WalletRecord
import com.translation.app.domain.repository.IWalletRepository
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl @Inject constructor(
        private val walletApi: WalletApiService
) : IWalletRepository {

    override suspend fun getBalance(): Result<Wallet> {
        return try {
            val res = walletApi.getBalance()
            if (res.code == 200 && res.data != null) Result.success(Wallet(res.data.balance, res.data.totalConsume))
            else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecords(type: Int?, page: Int, size: Int): Result<Pair<List<WalletRecord>, Long>> {
        return try {
            val res = walletApi.getRecords(type, page, size)
            if (res.code == 200 && res.data != null) Result.success(Pair(
                    res.data.records.map { WalletRecord(it.id, it.type, it.amount, it.balanceBefore, it.balanceAfter, it.description, it.createTime) },
                    res.data.total
            ))
            else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun recharge(payType: Int, amount: BigDecimal): Result<Order> {
        return try {
            val res = walletApi.recharge(RechargeRequest(payType, amount))
            if (res.code == 200 && res.data != null) Result.success(res.data.toDomain())
            else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRechargeStatus(orderNo: String): Result<Order> {
        return try {
            val res = walletApi.getRechargeStatus(orderNo)
            if (res.code == 200 && res.data != null) Result.success(res.data.toDomain())
            else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun com.translation.app.data.dto.wallet.OrderResponse.toDomain() = Order(id, orderNo, payType, amount, status, payTime, createTime, payData)
}
