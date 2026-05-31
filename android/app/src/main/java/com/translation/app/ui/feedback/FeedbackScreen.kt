package com.translation.app.ui.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.translation.app.domain.repository.IFeedbackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedbackUiState(val isLoading: Boolean = false, val error: String? = null, val success: Boolean = false)

@HiltViewModel
class FeedbackViewModel @Inject constructor(private val feedbackRepo: IFeedbackRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState

    fun submitFeedback(content: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            feedbackRepo.submitFeedback(content).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, success = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(navController: NavController, viewModel: FeedbackViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var content by remember { mutableStateOf("") }

    LaunchedEffect(uiState.success) { if (uiState.success) navController.popBackStack() }

    Scaffold(topBar = { TopAppBar(title = { Text("意见反馈") }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("返回") } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("请输入您的反馈意见") }, modifier = Modifier.fillMaxWidth().height(200.dp), maxLines = 10)
            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            }
            Button(onClick = { viewModel.submitFeedback(content) }, modifier = Modifier.fillMaxWidth(), enabled = content.isNotBlank() && !uiState.isLoading) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary) else Text("提交")
            }
        }
    }
}
