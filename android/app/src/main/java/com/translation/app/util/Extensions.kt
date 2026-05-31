package com.translation.app.util

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun BigDecimal?.orZero(): BigDecimal = this ?: BigDecimal.ZERO
fun BigDecimal.formatMoney(): String = setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()

fun String?.formatDateTime(): String {
    if (isNullOrBlank()) return ""
    return try {
        LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    } catch (e: Exception) {
        this
    }
}

fun String.truncate(maxLen: Int = 50): String = if (length <= maxLen) this else substring(0, maxLen) + "..."
