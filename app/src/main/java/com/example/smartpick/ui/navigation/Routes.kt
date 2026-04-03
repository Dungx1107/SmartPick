package com.example.smartpick.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Home : Routes("home")
    object ChatBot : Routes("chatbot")
    object Profile : Routes("profile")
    object Saved : Routes("saved")
}