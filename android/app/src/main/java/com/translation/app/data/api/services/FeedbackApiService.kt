package com.translation.app.data.api.services

import com.translation.app.data.dto.ApiResponse
import com.translation.app.data.dto.feedback.FeedbackRequest
import retrofit2.http.*

interface FeedbackApiService {
    @POST("/api/app/feedback")
    suspend fun submitFeedback(@Body request: FeedbackRequest): ApiResponse<Unit>
}