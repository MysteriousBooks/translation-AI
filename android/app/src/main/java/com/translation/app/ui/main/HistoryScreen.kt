package com.translation.app.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.translation.app.ui.components.EmptyState
import com.translation.app.ui.components.LoadingIndicator
import com.translation.app.ui.navigation.Screen
import com.translation.app.util.TranslateStatus

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: HistoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadHistory(refresh = true) }

    Scaffold(topBar = { TopAppBar(title = { Text("翻译历史") }) }) { padding ->
        when {
            uiState.translations.isEmpty() && uiState.isLoading -> LoadingIndicator(modifier = Modifier.padding(padding))
            uiState.translations.isEmpty() -> EmptyState("暂无翻译记录", modifier = Modifier.padding(padding))
            else -> Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                uiState.translations.forEach { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable { navController.navigate(Screen.HistoryDetail.createRoute(item.id)) }) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(item.sourceText, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
                            if (item.translatedText != null) {
                                Text(item.translatedText!!, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${item.sourceLang} -> ${item.targetLang}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(TranslateStatus.fromValue(item.status).label, style = MaterialTheme.typography.labelSmall, color = if (item.status == 2) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                if (uiState.hasMore) {
                    LaunchedEffect(uiState.translations.size) { viewModel.loadHistory() }
                }
            }
        }
    }
}
