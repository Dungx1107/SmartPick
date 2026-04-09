package com.example.smartpick.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Home : Routes("home")
    object ChatBot : Routes("chatbot")
    object Saved : Routes("saved")
    object Profile : Routes("profile")

    object EditProfile : Routes("edit_profile")


}