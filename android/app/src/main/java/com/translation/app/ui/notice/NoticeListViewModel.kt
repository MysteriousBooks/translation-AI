package com.translation.app.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translation.app.domain.model.Announcement
import com.translation.app.domain.repository.IAnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoticeListUiState(
        val notices: List<Announcement> = emptyList(),
        val isLoading: Boolean = false,
        val hasMore: Boolean = true,
        val page: Int = 1,
        val total: Long = 0
)

@HiltViewModel
class NoticeListViewModel @Inject constructor(private val announcementRepo: IAnnouncementRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(NoticeListUiState())
    val uiState: StateFlow<NoticeListUiState> = _uiState

    fun loadNotices(refresh: Boolean = false) {
        val page = if (refresh) 1 else _uiState.value.page
        if (!refresh && !_uiState.value.hasMore) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            announcementRepo.getAnnouncements(page, 20).onSuccess { (list, total) ->
                val current = _uiState.value.notices
                val newList = if (refresh) list else current + list
                _uiState.value = _uiState.value.copy(
                        notices = newList,
                        page = page + 1, total = total,
                        hasMore = newList.size < total,
                        isLoading = false
                )
            }.onFailure { _uiState.value = _uiState.value.copy(isLoading = false) }
        }
    }
}
