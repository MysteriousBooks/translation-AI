package com.translation.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.translation.app.ui.navigation.AppNavHost
import com.translation.app.ui.navigation.BottomNavItem
import com.translation.app.ui.theme.TranslationAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TranslationAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TranslationApp()
                }
            }
        }
    }
}

@Composable
fun TranslationApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomNavRoutes = BottomNavItem.entries.map { it.route }.toSet()
    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        BottomNavItem.entries.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                            NavigationBarItem(
                                    icon = { Icon(painterResource(id = item.iconRes), contentDescription = item.label) },
                                    label = { Text(item.label) },
                                    selected = selected,
                                    onClick = { navController.navigate(item.route) { popUpTo(navController.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true } }
                            )
                        }
                    }
                }
            }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AppNavHost(navController = navController)
        }
    }
}
