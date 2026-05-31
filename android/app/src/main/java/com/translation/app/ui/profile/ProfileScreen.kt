package com.translation.app.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.translation.app.ui.navigation.Screen
import com.translation.app.util.LoginType
import com.translation.app.util.formatMoney

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadUserInfo() }

    Scaffold(topBar = { TopAppBar(title = { Text("我的") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(uiState.user?.nickname ?: "未设置昵称", style = MaterialTheme.typography.titleMedium)
                        Text(uiState.user?.email
                                ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        val bal = uiState.user?.balance?.formatMoney() ?: "0.00"
                        Text("余额: Y $bal", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            ListItem(headlineContent = { Text("编辑资料") }, modifier = Modifier.clickable { navController.navigate(Screen.EditProfile.route) })
            ListItem(headlineContent = { Text("修改密码") }, modifier = Modifier.clickable { navController.navigate(Screen.ChangePassword.route) })
            ListItem(headlineContent = { Text("公告通知") }, modifier = Modifier.clickable { navController.navigate(Screen.NoticeList.route) })
            ListItem(headlineContent = { Text("意见反馈") }, modifier = Modifier.clickable { navController.navigate(Screen.Feedback.route) })
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.logout(); navController.navigate(Screen.Login.route) { popUpTo(0) } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("退出登录")
            }
        }
    }
}
