package com.translation.app.domain.repository

interface IRefundRepository {
    suspend fun applyRefund(orderId: Long, reason: String): Result<Unit>
}