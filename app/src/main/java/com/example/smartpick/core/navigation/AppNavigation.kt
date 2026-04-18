package com.example.smartpick.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartpick.R
import com.example.smartpick.features.auth.ui.AuthViewModel
import com.example.smartpick.features.chatbot.ui.ChatbotScreen
import com.example.smartpick.features.home.ui.HomeScreen
import com.example.smartpick.features.auth.ui.LoginScreen
import com.example.smartpick.features.home.ui.HomeScreenRoute
import com.example.smartpick.features.profile.ui.ProfileScreen
import com.example.smartpick.features.profile.ui.SavedCollectionScreen
import com.example.smartpick.features.profile.ui.EditProfileScreen
import kotlinx.coroutines.delay

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val currentUser by authViewModel.currentUser.collectAsState()// Lắng nghe thông tin User hiện tại
    val isInitializing by authViewModel.isInitializing.collectAsState()

    // Tự động điều hướng khi trạng thái đăng nhập thay đổi
    LaunchedEffect(currentUser) {
        if (currentUser != null && currentRoute == Routes.Login.route) {
            // Nếu đã có user mà đang ở màn Login -> Nhảy vào Home luôn
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Login.route) { inclusive = true }
            }
        }
    }
// NẾU ĐANG KHỞI TẠO: Hiện màn hình trắng hoặc Logo app
    if (isInitializing) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF1E3A8A))
            // Có thể thêm Logo SmartPick ở đây cho xịn
        }
    } else {
        Scaffold(
            topBar = {
                if (currentRoute != Routes.Login.route
                    && currentRoute != Routes.EditProfile.route
                ) { // Chỉ hiện TopBar nếu không phải màn Login

                    MainTopBar(
                        onMenuClick = {
                            // TODO: Mở navigation drawer
                        },
                        tagText = when (currentRoute) {
                            Routes.Home.route -> stringResource(R.string.app_name)
                            Routes.ChatBot.route -> stringResource(R.string.ai_curator)
                            Routes.Saved.route -> stringResource(R.string.saved)
                            Routes.Profile.route -> stringResource(R.string.profile)
                            else -> null
                        },
                        showCartBadge = currentRoute == Routes.Home.route // Chỉ hiện tag AI ASSISTANT ở màn Home
                    )
                }
            },
            bottomBar = {
                if (currentRoute != Routes.Login.route
                    && currentRoute != Routes.EditProfile.route
                ) {
                    MainBottomBar(
                        navController = navController,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                } // 1. Quay về màn hình đầu tiên (thường là Home) để tránh tích tụ stack
                                launchSingleTop =
                                    true //Tránh việc mở màn hình đó nhiều lần khi nhấn liên tục vào icon
                                restoreState = true  //Khôi phục lại trạng thái khi quay lại tab đó
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
                    HomeScreenRoute()
                }

                composable(Routes.ChatBot.route) {
                    ChatbotScreen()
                }

                composable(Routes.Saved.route) {
                    SavedCollectionScreen()
                }

                composable(Routes.Profile.route) {
                    ProfileScreen(navController)
                }

                composable(Routes.EditProfile.route) {
                    EditProfileScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}