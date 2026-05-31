package com.translation.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translation.app.domain.model.Translation
import com.translation.app.domain.repository.ITranslateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
        val translations: List<Translation> = emptyList(),
        val isLoading: Boolean = false,
        val page: Int = 1,
        val total: Long = 0,
        val hasMore: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
        private val translateRepo: ITranslateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    fun loadHistory(refresh: Boolean = false) {
        if (_uiState.value.isLoading) return
        val page = if (refresh) 1 else _uiState.value.page
        if (!refresh && !_uiState.value.hasMore) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            translateRepo.getHistory(page, 20).onSuccess { (list, total) ->
                val current = _uiState.value.translations
                val newList = if (refresh) list else current + list
                _uiState.value = _uiState.value.copy(
                        translations = newList,
                        page = page + 1, total = total,
                        hasMore = newList.size < total,
                        isLoading = false
                )
            }.onFailure { _uiState.value = _uiState.value.copy(isLoading = false) }
        }
    }
}
