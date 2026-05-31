package com.translation.app.ui.main

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.translation.app.ui.components.LanguageSelector

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.loadRecentHistory() }

    Scaffold(topBar = { TopAppBar(title = { Text("翻译") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LanguageSelector(selectedCode = uiState.sourceLang, onLanguageSelected = viewModel::setSourceLang, label = "源语言", modifier = Modifier.weight(1f))
                IconButton(onClick = viewModel::swapLanguages) { Icon(Icons.Default.SwapHoriz, "交换语言") }
                LanguageSelector(selectedCode = uiState.targetLang, onLanguageSelected = viewModel::setTargetLang, label = "目标语言", modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = uiState.sourceText, onValueChange = viewModel::setSourceText, label = { Text("输入要翻译的文本") }, modifier = Modifier.fillMaxWidth().height(150.dp), maxLines = 8)
            Button(onClick = viewModel::translate, modifier = Modifier.fillMaxWidth(), enabled = uiState.sourceText.isNotBlank() && !uiState.isTranslating) {
                if (uiState.isTranslating) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary) else Text("翻译")
            }
            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            if (uiState.translatedText != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("翻译结果", style = MaterialTheme.typography.titleSmall)
                            TextButton(onClick = {
                                val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clip.setPrimaryClip(android.content.ClipData.newPlainText("translation", uiState.translatedText))
                            }) { Text("复制") }
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(uiState.translatedText!!, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            if (uiState.recentTranslations.isNotEmpty()) {
                Text("最近翻译", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                uiState.recentTranslations.forEach { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(item.sourceText, maxLines = 1, style = MaterialTheme.typography.bodySmall)
                            Text(item.translatedText
                                    ?: "", maxLines = 1, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
