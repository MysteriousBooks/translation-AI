package com.translation.app.domain.model

import java.math.BigDecimal

data class Translation(
        val id: Long, val sourceLang: String, val targetLang: String,
        val sourceText: String, val translatedText: String?,
        val charCount: Int, val costAmount: BigDecimal,
        val pricePerKchar: BigDecimal, val status: Int,
        val errorMsg: String?, val createTime: String?
)