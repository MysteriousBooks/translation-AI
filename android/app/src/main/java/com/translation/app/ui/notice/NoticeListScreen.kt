package com.translation.app.ui.notice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.translation.app.ui.navigation.Screen

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun NoticeListScreen(navController: NavController, viewModel: NoticeListViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadNotices(refresh = true) }

    Scaffold(topBar = { TopAppBar(title = { Text("公告通知") }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("返回") } }) }) { padding ->
        if (uiState.notices.isEmpty() && !uiState.isLoading) {
            com.translation.app.ui.components.EmptyState("暂无公告", modifier = Modifier.padding(padding))
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                uiState.notices.forEach { notice ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable { navController.navigate(Screen.NoticeDetail.createRoute(notice.id)) }) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(notice.title, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            notice.publishTime?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
                if (uiState.hasMore) {
                    LaunchedEffect(uiState.notices.size) { viewModel.loadNotices() }
                }
            }
        }
    }
}
