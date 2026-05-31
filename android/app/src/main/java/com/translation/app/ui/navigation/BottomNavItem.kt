package com.translation.app.ui.navigation

import com.translation.app.R

enum class BottomNavItem(
        val route: String,
        val label: String,
        val iconRes: Int
) {
    HOME(Screen.Home.route, "首页", R.drawable.ic_home),
    HISTORY(Screen.History.route, "历史", R.drawable.ic_history),
    WALLET(Screen.Wallet.route, "钱包", R.drawable.ic_wallet),
    PROFILE(Screen.Profile.route, "我的", R.drawable.ic_profile)
}
