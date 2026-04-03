package com.example.smartpick.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartpick.ui.screens.chatbot.ChatbotScreen
import com.example.smartpick.ui.screens.home.HomeScreen
import com.example.smartpick.ui.theme.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Routes.Login.route) {// Logic: Chỉ hiện nếu không phải màn Login
                MainBottomBar(
                    navController = navController,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            } // 1. Quay về màn hình đầu tiên (thường là Home) để tránh tích tụ stack
                            launchSingleTop = true // 2. Tránh việc mở lại chính màn hình đó nhiều lần khi nhấn liên tục vào icon
                            restoreState = true  // 3. Khôi phục lại trạng thái (ví dụ: vị trí cuộn trang) khi quay lại tab đó
                        }
                    }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.Login.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Routes.Login.route) {
                LoginScreen(
                    onNavigateToHome = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToSignUp = {}
                )
            }

            composable(Routes.Home.route) {
                HomeScreen()
            }

            composable(Routes.ChatBot.route) {
                ChatbotScreen()
            }
        }
    }
}