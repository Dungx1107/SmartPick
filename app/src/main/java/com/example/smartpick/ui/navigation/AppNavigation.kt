package com.example.smartpick.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartpick.ui.screens.home.HomeScreen
import com.example.smartpick.ui.theme.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Điểm bắt đầu (startDestination) là màn hình Login
    NavHost(navController = navController, startDestination = Routes.Login.route) {

        composable(Routes.Login.route) {
            LoginScreen(
                // Khi đăng nhập xong, gọi hàm này để nhảy sang Home
                onNavigateToHome = {
                    navController.navigate(Routes.Home.route) {
                        // Xóa LoginScreen khỏi ngăn xếp để ấn Back không quay lại được
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {}
            )
        }

        composable(Routes.Home.route) {
            HomeScreen()
        }
    }
}