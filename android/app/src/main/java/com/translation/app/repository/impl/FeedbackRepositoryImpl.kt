package com.translation.app.repository.impl

import com.translation.app.data.api.services.FeedbackApiService
import com.translation.app.data.dto.feedback.FeedbackRequest
import com.translation.app.domain.repository.IFeedbackRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepositoryImpl @Inject constructor(
        private val feedbackApi: FeedbackApiService
) : IFeedbackRepository {

    override suspend fun submitFeedback(content: String): Result<Unit> {
        return try {
            val res = feedbackApi.submitFeedback(FeedbackRequest(content))
            if (res.code == 200) Result.success(Unit) else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
