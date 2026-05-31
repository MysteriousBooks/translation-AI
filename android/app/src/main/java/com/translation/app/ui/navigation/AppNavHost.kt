package com.translation.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.translation.app.ui.auth.ForgotPasswordScreen
import com.translation.app.ui.auth.LoginScreen
import com.translation.app.ui.auth.RegisterScreen
import com.translation.app.ui.feedback.FeedbackScreen
import com.translation.app.ui.main.HistoryDetailScreen
import com.translation.app.ui.main.HistoryScreen
import com.translation.app.ui.main.HomeScreen
import com.translation.app.ui.notice.NoticeDetailScreen
import com.translation.app.ui.notice.NoticeListScreen
import com.translation.app.ui.profile.ChangePasswordScreen
import com.translation.app.ui.profile.EditProfileScreen
import com.translation.app.ui.profile.ProfileScreen
import com.translation.app.ui.wallet.RechargeResultScreen
import com.translation.app.ui.wallet.RechargeScreen
import com.translation.app.ui.wallet.WalletScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.History.route) { HistoryScreen(navController) }
        composable(
                Screen.HistoryDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { HistoryDetailScreen(navController) }
        composable(Screen.Wallet.route) { WalletScreen(navController) }
        composable(Screen.Recharge.route) { RechargeScreen(navController) }
        composable(
                Screen.RechargeResult.route,
                arguments = listOf(navArgument("orderNo") { type = NavType.StringType })
        ) { RechargeResultScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.EditProfile.route) { EditProfileScreen(navController) }
        composable(Screen.ChangePassword.route) { ChangePasswordScreen(navController) }
        composable(Screen.NoticeList.route) { NoticeListScreen(navController) }
        composable(
                Screen.NoticeDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { NoticeDetailScreen(navController) }
        composable(Screen.Feedback.route) { FeedbackScreen(navController) }
    }
}
