package com.translation.app.ui.notice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translation.app.domain.model.Announcement
import com.translation.app.domain.repository.IAnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoticeDetailUiState(val notice: Announcement? = null, val isLoading: Boolean = false, val error: String? = null)

@HiltViewModel
class NoticeDetailViewModel @Inject constructor(
        savedStateHandle: SavedStateHandle,
        private val announcementRepo: IAnnouncementRepository
) : ViewModel() {
    private val id: Long = savedStateHandle["id"] ?: 0L
    private val _uiState = MutableStateFlow(NoticeDetailUiState(isLoading = true))
    val uiState: StateFlow<NoticeDetailUiState> = _uiState

    init {
        loadNotice()
    }

    private fun loadNotice() {
        viewModelScope.launch {
            announcementRepo.getAnnouncement(id).onSuccess {
                _uiState.value = _uiState.value.copy(notice = it, isLoading = false)
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message, isLoading = false)
            }
        }
    }
}
