package com.translation.app.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.translation.app.ui.navigation.Screen
import kotlinx.coroutines.delay

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RechargeResultScreen(navController: NavController, viewModel: WalletViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val orderNo = viewModel.getOrderNo()
    var paid by remember { mutableStateOf(false) }
    var polling by remember { mutableStateOf(true) }

    LaunchedEffect(orderNo) {
        if (orderNo.isNotEmpty()) {
            repeat(10) {
                if (!paid) {
                    viewModel.checkRechargeStatus(orderNo) { paid = true }
                    delay(3000)
                }
            }
            polling = false
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("充值结果") }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when {
                    paid -> {
                        Text("充值成功!", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack(Screen.Recharge.route, inclusive = true) }) { Text("返回钱包") }
                    }
                    polling -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("正在查询支付结果...", style = MaterialTheme.typography.bodyLarge)
                    }
                    else -> {
                        Text("支付结果查询超时", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) { Text("返回") }
                    }
                }
            }
        }
    }
}
