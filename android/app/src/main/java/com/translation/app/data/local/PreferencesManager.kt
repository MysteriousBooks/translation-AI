package com.translation.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class PreferencesManager @Inject constructor(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val NICKNAME_KEY = stringPreferencesKey("nickname")
        private val AVATAR_KEY = stringPreferencesKey("avatar")
    }

    fun getToken(): Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clearAuth() {
        context.dataStore.edit { it.clear() }
    }

    fun getUserId(): Flow<Long?> = context.dataStore.data.map { it[USER_ID_KEY]?.toLongOrNull() }
    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { it[USER_ID_KEY] = userId.toString() }
    }

    fun getNickname(): Flow<String?> = context.dataStore.data.map { it[NICKNAME_KEY] }
    suspend fun saveNickname(nickname: String) {
        context.dataStore.edit { it[NICKNAME_KEY] = nickname }
    }

    fun getAvatar(): Flow<String?> = context.dataStore.data.map { it[AVATAR_KEY] }
    suspend fun saveAvatar(avatar: String) {
        context.dataStore.edit { it[AVATAR_KEY] = avatar }
    }

    fun isLoggedIn(): Flow<Boolean> = getToken().map { !it.isNullOrBlank() }
}