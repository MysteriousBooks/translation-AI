package com.translation.app.domain.repository

interface IFeedbackRepository {
    suspend fun submitFeedback(content: String): Result<Unit>
}