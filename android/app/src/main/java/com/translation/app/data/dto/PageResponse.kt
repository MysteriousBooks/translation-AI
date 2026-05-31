package com.translation.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageResponse<T>(
        val total: Long = 0,
        val page: Long = 1,
        val size: Long = 10,
        val records: List<T> = emptyList()
)
