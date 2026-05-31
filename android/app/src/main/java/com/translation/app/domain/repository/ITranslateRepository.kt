package com.translation.app.domain.repository

import com.translation.app.domain.model.Translation

interface ITranslateRepository {
    suspend fun translate(sourceLang: String, targetLang: String, sourceText: String): Result<Translation>
    suspend fun getHistory(page: Int, size: Int): Result<Pair<List<Translation>, Long>>
    suspend fun getTranslation(id: Long): Result<Translation>
}