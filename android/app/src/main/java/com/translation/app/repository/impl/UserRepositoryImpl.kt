package com.translation.app.repository.impl

import com.translation.app.data.api.services.UserApiService
import com.translation.app.data.dto.user.*
import com.translation.app.data.local.PreferencesManager
import com.translation.app.domain.model.User
import com.translation.app.domain.repository.IUserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
        private val userApi: UserApiService,
        private val prefs: PreferencesManager
) : IUserRepository {

    override suspend fun getUserInfo(): Result<User> {
        return try {
            val res = userApi.getUserInfo()
            if (res.code == 200 && res.data != null) {
                val d = res.data
                prefs.saveNickname(d.nickname ?: "")
                prefs.saveAvatar(d.avatar ?: "")
                Result.success(User(d.id, d.email, d.nickname, d.avatar, d.phone, d.loginType, d.balance, d.totalConsume, d.createTime))
            } else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(nickname: String?, avatar: String?, phone: String?): Result<Unit> {
        return try {
            val res = userApi.updateUserInfo(UpdateUserRequest(nickname, avatar, phone))
            if (res.code == 200) {
                nickname?.let { prefs.saveNickname(it) }
                avatar?.let { prefs.saveAvatar(it) }
                Result.success(Unit)
            } else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            val res = userApi.changePassword(ChangePasswordRequest(oldPassword, newPassword))
            if (res.code == 200) Result.success(Unit) else Result.failure(Exception(res.msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
