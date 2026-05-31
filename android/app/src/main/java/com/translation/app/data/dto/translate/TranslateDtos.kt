package com.translation.app.data.dto.translate

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class TranslateRequest(val sourceLang: String, val targetLang: String, val sourceText: String)

@Serializable
data class TranslateResultResponse(
        val id: Long, val sourceLang: String, val targetLang: String,
        val sourceText: String, val translatedText: String? = null,
        val charCount: Int = 0, val costAmount: BigDecimal = BigDecimal.ZERO,
        val pricePerKchar: BigDecimal = BigDecimal.ZERO, val status: Int = 0,
        val errorMsg: String? = null, val createTime: String? = null
)
