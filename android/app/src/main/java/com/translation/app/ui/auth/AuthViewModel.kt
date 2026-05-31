package com.translation.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translation.app.domain.repository.IAuthRepository
import com.translation.app.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val loginSuccess: Boolean = false,
        val codeSent: Boolean = false,
        val codeCountdown: Int = 0
)

@HiltViewModel
class AuthViewModel @Inject constructor(
        private val authRepo: IAuthRepository,
        private val userRepo: IUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun loginByEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepo.loginByEmail(email, password).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun register(email: String, password: String, code: String, nickname: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepo.register(email, password, code, nickname).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun forgotPassword(email: String, code: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepo.forgotPassword(email, code, newPassword).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun sendCode(email: String) {
        viewModelScope.launch {
            authRepo.sendCode(email).onSuccess {
                _uiState.value = _uiState.value.copy(codeSent = true, codeCountdown = 60)
                startCountdown()
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message)
            }
        }
    }

    fun loginByWechat(code: String, nickname: String?, avatar: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepo.loginByWechat(code, nickname, avatar).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun loginByAlipay(code: String, nickname: String?, avatar: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepo.loginByAlipay(code, nickname, avatar).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, loginSuccess = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (_uiState.value.codeCountdown > 0) {
                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(codeCountdown = _uiState.value.codeCountdown - 1)
            }
        }
    }
}
