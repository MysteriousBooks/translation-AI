package com.translation.app.data.dto.notice

import kotlinx.serialization.Serializable

@Serializable
data class AnnouncementResponse(
        val id: Long, val title: String, val content: String,
        val type: Int = 0, val status: Int = 0,
        val publishTime: String? = null, val createTime: String? = null
)
