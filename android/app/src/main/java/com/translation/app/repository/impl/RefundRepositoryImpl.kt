package com.translation.app.repository.impl

import com.translation.app.data.api.services.RefundApiService
import com.translation.app.data.dto.refund.RefundApplyRequest
import com.translation.app.domain.repository.IRefundRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefundRepositoryImpl @Inject constructor(
        private val refundApi: RefundApiService
) : IRefundRepository {

    override suspend fun applyRefund(orderId: Long, reason: String): Result<Unit> {
        return try {
            val res = refundApi.applyRefund(RefundApplyRequest(orderId, reason))
            if (res.code == 200) Result.success(Unit) else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
