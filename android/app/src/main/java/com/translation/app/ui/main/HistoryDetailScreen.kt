package com.translation.app.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.translation.app.ui.components.LoadingIndicator
import com.translation.app.ui.components.EmptyState
import com.translation.app.util.TranslateStatus
import com.translation.app.util.formatMoney

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(navController: NavController, viewModel: HistoryDetailViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("翻译详情") }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("返回") } }) }) { padding ->
        when {
            uiState.translation == null && uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(padding))
            uiState.error != null -> EmptyState(message = uiState.error ?: "加载失败", modifier = Modifier.padding(padding))
            uiState.translation != null -> {
                val t = uiState.translation!!
                Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("原文", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(t.sourceText, style = MaterialTheme.typography.bodyLarge)
                    Divider()
                    Text("译文", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(t.translatedText
                            ?: "翻译中...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${t.sourceLang} -> ${t.targetLang}", style = MaterialTheme.typography.bodySmall)
                        Text(TranslateStatus.fromValue(t.status).label, style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("字符数: ${t.charCount}", style = MaterialTheme.typography.bodySmall)
                        Text("费用: ${t.costAmount.formatMoney()}", style = MaterialTheme.typography.bodySmall)
                    }
                    t.createTime?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    t.errorMsg?.let { Text("错误: $it", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}
