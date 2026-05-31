package com.translation.app.repository.impl

import com.translation.app.data.api.services.AnnouncementApiService
import com.translation.app.domain.model.Announcement
import com.translation.app.domain.repository.IAnnouncementRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnnouncementRepositoryImpl @Inject constructor(
        private val announcementApi: AnnouncementApiService
) : IAnnouncementRepository {

    override suspend fun getAnnouncements(page: Int, size: Int): Result<Pair<List<Announcement>, Long>> {
        return try {
            val res = announcementApi.getNoticeList(page, size)
            if (res.code == 200 && res.data != null) Result.success(Pair(
                    res.data.records.map { Announcement(it.id, it.title, it.content, it.type, it.status, it.publishTime, it.createTime) },
                    res.data.total
            ))
            else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAnnouncement(id: Long): Result<Announcement> {
        // Backend does not have a single-notice endpoint, fetch from list
        return try {
            val res = announcementApi.getNoticeList(1, 100)
            if (res.code == 200 && res.data != null) {
                val notice = res.data.records.find { it.id == id }
                if (notice != null) Result.success(Announcement(notice.id, notice.title, notice.content, notice.type, notice.status, notice.publishTime, notice.createTime))
                else Result.failure(Exception("公告不存在"))
            } else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
