package com.translation.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object History : Screen("history")
    object HistoryDetail : Screen("history/{id}") {
        fun createRoute(id: Long) = "history/$id"
    }

    object Wallet : Screen("wallet")
    object Recharge : Screen("recharge")
    object RechargeResult : Screen("recharge_result/{orderNo}") {
        fun createRoute(orderNo: String) = "recharge_result/$orderNo"
    }

    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object ChangePassword : Screen("change_password")
    object NoticeList : Screen("notices")
    object NoticeDetail : Screen("notice/{id}") {
        fun createRoute(id: Long) = "notice/$id"
    }

    object Feedback : Screen("feedback")
}
