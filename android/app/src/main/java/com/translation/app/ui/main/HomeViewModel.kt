package com.translation.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.translation.app.domain.model.Translation
import com.translation.app.domain.repository.ITranslateRepository
import com.translation.app.util.Languages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
        val sourceLang: String = "zh",
        val targetLang: String = "en",
        val sourceText: String = "",
        val translatedText: String? = null,
        val isTranslating: Boolean = false,
        val error: String? = null,
        val recentTranslations: List<Translation> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val translateRepo: ITranslateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun setSourceLang(code: String) {
        _uiState.value = _uiState.value.copy(sourceLang = code)
    }

    fun setTargetLang(code: String) {
        _uiState.value = _uiState.value.copy(targetLang = code)
    }

    fun setSourceText(text: String) {
        _uiState.value = _uiState.value.copy(sourceText = text)
    }

    fun swapLanguages() {
        val current = _uiState.value
        _uiState.value = current.copy(
                sourceLang = current.targetLang,
                targetLang = current.sourceLang,
                sourceText = current.translatedText ?: current.sourceText,
                translatedText = if (current.translatedText != null) current.sourceText else null
        )
    }

    fun translate() {
        val state = _uiState.value
        if (state.sourceText.isBlank()) return
        viewModelScope.launch {
            _uiState.value = state.copy(isTranslating = true, error = null)
            translateRepo.translate(state.sourceLang, state.targetLang, state.sourceText).onSuccess {
                _uiState.value = _uiState.value.copy(isTranslating = false, translatedText = it.translatedText, error = if (it.status == 2) it.errorMsg else null)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isTranslating = false, error = it.message)
            }
        }
    }

    fun loadRecentHistory() {
        viewModelScope.launch {
            translateRepo.getHistory(1, 5).onSuccess { (list, _) ->
                _uiState.value = _uiState.value.copy(recentTranslations = list)
            }
        }
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(translatedText = null, sourceText = "")
    }

    fun langName(code: String): String = Languages.find { it.first == code }?.second ?: code
}
