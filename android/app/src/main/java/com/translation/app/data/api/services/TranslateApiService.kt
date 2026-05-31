package com.translation.app.data.api.services

import com.translation.app.data.dto.ApiResponse
import com.translation.app.data.dto.PageResponse
import com.translation.app.data.dto.translate.*
import retrofit2.http.*

interface TranslateApiService {
    @POST("/api/app/translate")
    suspend fun translate(@Body request: TranslateRequest): ApiResponse<TranslateResultResponse>

    @GET("/api/app/translate/history")
    suspend fun getHistory(@Query("page") page: Int, @Query("size") size: Int): ApiResponse<PageResponse<TranslateResultResponse>>

    @GET("/api/app/translate/{id}")
    suspend fun getDetail(@Path("id") id: Long): ApiResponse<TranslateResultResponse>
}