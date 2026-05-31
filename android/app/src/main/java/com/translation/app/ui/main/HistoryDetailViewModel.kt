package com.translation.app.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translation.app.domain.model.Translation
import com.translation.app.domain.repository.ITranslateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryDetailUiState(val translation: Translation? = null, val isLoading: Boolean = false, val error: String? = null)

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
        savedStateHandle: SavedStateHandle,
        private val translateRepo: ITranslateRepository
) : ViewModel() {
    private val id: Long = savedStateHandle["id"] ?: 0L
    private val _uiState = MutableStateFlow(HistoryDetailUiState(isLoading = true))
    val uiState: StateFlow<HistoryDetailUiState> = _uiState

    init {
        loadTranslation()
    }

    private fun loadTranslation() {
        viewModelScope.launch {
            translateRepo.getTranslation(id).onSuccess { _uiState.value = _uiState.value.copy(translation = it, isLoading = false) }
                    .onFailure { _uiState.value = _uiState.value.copy(error = it.message, isLoading = false) }
        }
    }
}
