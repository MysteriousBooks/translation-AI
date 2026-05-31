package com.translation.app.domain.repository

import com.translation.app.domain.model.User

interface IUserRepository {
    suspend fun getUserInfo(): Result<User>
    suspend fun updateUser(nickname: String?, avatar: String?, phone: String?): Result<Unit>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
}