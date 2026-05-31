package com.translation.app.domain.repository

import com.translation.app.domain.model.Announcement

interface IAnnouncementRepository {
    suspend fun getAnnouncements(page: Int, size: Int): Result<Pair<List<Announcement>, Long>>
    suspend fun getAnnouncement(id: Long): Result<Announcement>
}
