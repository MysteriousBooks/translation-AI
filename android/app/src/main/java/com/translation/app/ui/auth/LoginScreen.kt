package com.translation.app.ui.auth

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.translation.app.ui.components.ErrorDialog
import com.translation.app.ui.navigation.Screen
import com.translation.app.util.SocialLoginManager
import javax.inject.Inject

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val socialLoginManager = (context.applicationContext as com.translation.app.App).socialLoginManager

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("登录") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(40.dp))
            Text("翻译服务", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("邮箱") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true)
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("密码") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), singleLine = true)
            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { viewModel.loginByEmail(email, password) }, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary) else Text("登录")
            }
            TextButton(onClick = { navController.navigate(Screen.Register.route) }) { Text("没有账号？注册") }
            TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) { Text("忘记密码？") }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Button(
                    onClick = {
                        socialLoginManager.wechatLogin(activity, object : SocialLoginManager.SocialLoginCallback {
                            override fun onSuccess(code: String, nickname: String?, avatar: String?) {
                                viewModel.loginByWechat(code, nickname, avatar)
                            }

                            override fun onError(message: String) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) { Text("微信登录", color = MaterialTheme.colorScheme.onPrimaryContainer) }
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                    onClick = {
                        socialLoginManager.alipayLogin(activity, object : SocialLoginManager.SocialLoginCallback {
                            override fun onSuccess(code: String, nickname: String?, avatar: String?) {
                                viewModel.loginByAlipay(code, nickname, avatar)
                            }

                            override fun onError(message: String) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) { Text("支付宝登录", color = MaterialTheme.colorScheme.onTertiaryContainer) }
        }
    }
    if (uiState.error != null) {
        ErrorDialog(message = uiState.error!!, onDismiss = viewModel::clearError)
    }
}
