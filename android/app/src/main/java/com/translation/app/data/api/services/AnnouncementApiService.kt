package com.translation.app.data.api.services

import com.translation.app.data.dto.ApiResponse
import com.translation.app.data.dto.PageResponse
import com.translation.app.data.dto.notice.AnnouncementResponse
import retrofit2.http.*

interface AnnouncementApiService {
    @GET("/api/app/notice/list")
    suspend fun getNoticeList(@Query("page") page: Int, @Query("size") size: Int): ApiResponse<PageResponse<AnnouncementResponse>>
}
