package com.translation.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translation.app.data.local.PreferencesManager
import com.translation.app.domain.model.User
import com.translation.app.domain.repository.IAuthRepository
import com.translation.app.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
        val user: User? = null,
        val nickname: String? = null,
        val avatar: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val updateSuccess: Boolean = false,
        val passwordChanged: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
        private val userRepo: IUserRepository,
        private val authRepo: IAuthRepository,
        private val prefs: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadUserInfo() {
        viewModelScope.launch {
            userRepo.getUserInfo().onSuccess {
                _uiState.value = _uiState.value.copy(user = it, nickname = it.nickname, avatar = it.avatar)
            }
        }
    }

    fun updateProfile(nickname: String?, avatar: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            userRepo.updateUser(nickname, avatar, null).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, updateSuccess = true)
                loadUserInfo()
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            userRepo.changePassword(oldPassword, newPassword).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, passwordChanged = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepo.logout() }
    }
}
