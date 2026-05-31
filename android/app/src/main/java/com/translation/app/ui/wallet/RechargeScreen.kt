package com.translation.app.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.translation.app.ui.components.PayMethodSelector
import com.translation.app.ui.navigation.Screen
import com.translation.app.util.RechargeAmounts
import java.math.BigDecimal

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(navController: NavController, viewModel: WalletViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedAmount by remember { mutableStateOf("10") }
    var payType by remember { mutableIntStateOf(1) }
    var customAmount by remember { mutableStateOf("") }

    LaunchedEffect(uiState.rechargeOrder) {
        uiState.rechargeOrder?.let { order ->
            navController.navigate(Screen.RechargeResult.createRoute(order.orderNo))
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("充值") }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("返回") } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("选择充值金额", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                RechargeAmounts.forEach { amount ->
                    val selected = selectedAmount == amount && customAmount.isBlank()
                    OutlinedButton(onClick = { selectedAmount = amount; customAmount = "" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)) {
                        Text(amount)
                    }
                }
            }
            OutlinedTextField(value = customAmount, onValueChange = { customAmount = it; selectedAmount = "" }, label = { Text("自定义金额") }, modifier = Modifier.fillMaxWidth())
            Divider()
            Text("支付方式", style = MaterialTheme.typography.titleMedium)
            PayMethodSelector(selectedPayType = payType, onPayTypeSelected = { payType = it })
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                val amountStr = if (customAmount.isNotBlank()) customAmount else selectedAmount
                try {
                    val amount = BigDecimal(amountStr)
                    if (amount > BigDecimal.ZERO) viewModel.recharge(payType, amount)
                } catch (_: NumberFormatException) {
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary) else Text("确认充值")
            }
            if (uiState.rechargeError != null) {
                Text(uiState.rechargeError!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
