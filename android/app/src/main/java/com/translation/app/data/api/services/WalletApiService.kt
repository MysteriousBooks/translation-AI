package com.translation.app.data.api.services

import com.translation.app.data.dto.ApiResponse
import com.translation.app.data.dto.PageResponse
import com.translation.app.data.dto.wallet.*
import retrofit2.http.*

interface WalletApiService {
    @GET("/api/app/wallet/balance")
    suspend fun getBalance(): ApiResponse<WalletResponse>

    @GET("/api/app/wallet/records")
    suspend fun getRecords(@Query("type") type: Int?, @Query("page") page: Int, @Query("size") size: Int): ApiResponse<PageResponse<WalletRecordResponse>>

    @POST("/api/app/wallet/recharge")
    suspend fun recharge(@Body request: RechargeRequest): ApiResponse<OrderResponse>

    @GET("/api/app/wallet/recharge/status/{orderNo}")
    suspend fun getRechargeStatus(@Path("orderNo") orderNo: String): ApiResponse<OrderResponse>
}