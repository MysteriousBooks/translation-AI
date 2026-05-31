package com.translation.app.domain.model

data class Announcement(
        val id: Long, val title: String, val content: String,
        val type: Int, val status: Int,
        val publishTime: String?, val createTime: String?
)