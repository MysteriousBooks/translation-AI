package com.translation.app.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.translation.app.ui.navigation.Screen
import com.translation.app.util.WalletRecordType
import com.translation.app.util.formatMoney

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(navController: NavController, viewModel: WalletViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadBalance(); viewModel.loadRecords(refresh = true) }

    Scaffold(topBar = { TopAppBar(title = { Text("钱包") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("账户余额", style = MaterialTheme.typography.titleSmall)
                    val bal = uiState.wallet?.balance?.formatMoney() ?: "0.00"
                    Text("Y $bal", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { navController.navigate(Screen.Recharge.route) }, modifier = Modifier.fillMaxWidth()) { Text("充值") }
                }
            }
            Text("交易记录", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp))
            if (uiState.records.isEmpty() && !uiState.isLoading) {
                com.translation.app.ui.components.EmptyState("暂无交易记录")
            } else {
                uiState.records.forEach { record ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(WalletRecordType.fromValue(record.type).label, style = MaterialTheme.typography.bodyMedium)
                                record.description?.let { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall) }
                                record.createTime?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                            val amt = record.amount.formatMoney()
                            Text("Y $amt", style = MaterialTheme.typography.bodyLarge, color = if (record.type == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                        }
                    }
                }
                if (uiState.hasMore) {
                    LaunchedEffect(uiState.records.size) { viewModel.loadRecords() }
                }
            }
        }
    }
}
