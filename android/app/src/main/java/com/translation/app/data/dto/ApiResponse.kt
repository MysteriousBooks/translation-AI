package com.translation.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
        val code: Int = 0,
        val msg: String = "",
        val data: T? = null
)
