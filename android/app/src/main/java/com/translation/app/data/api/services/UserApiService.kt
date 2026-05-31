package com.translation.app.data.api.services

import com.translation.app.data.dto.ApiResponse
import com.translation.app.data.dto.user.*
import retrofit2.http.*

interface UserApiService {
    @GET("/api/app/user/info")
    suspend fun getUserInfo(): ApiResponse<UserInfoResponse>

    @PUT("/api/app/user/info")
    suspend fun updateUserInfo(@Body request: UpdateUserRequest): ApiResponse<Unit>

    @PUT("/api/app/user/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<Unit>
}