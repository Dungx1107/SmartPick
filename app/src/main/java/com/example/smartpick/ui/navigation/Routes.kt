package com.example.smartpick.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login_screen")
    object Home : Routes("home_screen")

}