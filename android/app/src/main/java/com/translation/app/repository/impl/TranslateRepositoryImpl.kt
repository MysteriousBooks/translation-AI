package com.translation.app.repository.impl

import com.translation.app.data.api.services.TranslateApiService
import com.translation.app.data.dto.translate.TranslateRequest
import com.translation.app.domain.model.Translation
import com.translation.app.domain.repository.ITranslateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslateRepositoryImpl @Inject constructor(
        private val translateApi: TranslateApiService
) : ITranslateRepository {

    override suspend fun translate(sourceLang: String, targetLang: String, sourceText: String): Result<Translation> {
        return try {
            val res = translateApi.translate(TranslateRequest(sourceLang, targetLang, sourceText))
            if (res.code == 200 && res.data != null) Result.success(res.data.toDomain())
            else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHistory(page: Int, size: Int): Result<Pair<List<Translation>, Long>> {
        return try {
            val res = translateApi.getHistory(page, size)
            if (res.code == 200 && res.data != null) Result.success(Pair(res.data.records.map { it.toDomain() }, res.data.total))
            else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTranslation(id: Long): Result<Translation> {
        return try {
            val res = translateApi.getDetail(id)
            if (res.code == 200 && res.data != null) Result.success(res.data.toDomain())
            else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun com.translation.app.data.dto.translate.TranslateResultResponse.toDomain() = Translation(
            id, sourceLang, targetLang, sourceText, translatedText, charCount, costAmount, pricePerKchar, status, errorMsg, createTime
    )
}
