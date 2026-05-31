package com.translation.app.data.api.services

import com.translation.app.data.dto.ApiResponse
import com.translation.app.data.dto.refund.RefundApplyRequest
import retrofit2.http.*

interface RefundApiService {
    @POST("/api/app/refund/apply")
    suspend fun applyRefund(@Body request: RefundApplyRequest): ApiResponse<Unit>
}