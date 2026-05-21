package com.example.smartpick

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.settings.viewmodel.SettingsViewModel
import com.example.smartpick.navigation.AppNavigation
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Bổ sung đoạn này để chủ động lấy FCM Token hiện tại
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Lấy FCM token thất bại", task.exception)
                return@addOnCompleteListener
            }
            // Lấy token thành công
            val token = task.result
            Log.d("FCM_TOKEN", "Token chủ động lấy được: $token")

            // Ghi chú: Sau này bạn có thể gọi ViewModel ở đây để đẩy token lên Supabase
        }

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

            SmartPickTheme(darkTheme = isDarkMode) {
                AppNavigation()
            }
//            SmartPickTheme {
//                AppNavigation()
//            }
        }
    }
}
