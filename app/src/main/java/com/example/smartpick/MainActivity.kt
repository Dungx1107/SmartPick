package com.example.smartpick

import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.settings.data.LanguageRepository
import com.example.smartpick.features.settings.viewmodel.SettingsViewModel
import com.example.smartpick.navigation.AppNavigation
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var languageRepository: LanguageRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Hilt sẽ khởi tạo các dependencies tại đây
        enableEdgeToEdge()

        // Lấy FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM_TOKEN", "Token: ${task.result}")
            }
        }

        setContent {
            // Khởi tạo ViewModel thông qua Hilt
            val settingsViewModel: SettingsViewModel = hiltViewModel()

            // Collect dữ liệu từ Flow dưới dạng State để Compose tự động cập nhật UI
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState(initial = false)
            val currentLanguage by settingsViewModel.currentLanguage.collectAsState(initial = "vi")

            // Sử dụng LaunchedEffect để xử lý thay đổi Locale mà không chặn luồng chính
            LaunchedEffect(currentLanguage) {
                setAppLocale(currentLanguage)
            }

            SmartPickTheme(darkTheme = isDarkMode) {
                AppNavigation()
            }
        }
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = this.resources
        val configuration = resources.configuration

        // Cập nhật cấu hình cho Activity
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Cập nhật cho Application Context
        val appResources = applicationContext.resources
        val appConfig = appResources.configuration
        appConfig.setLocale(locale)
        appConfig.setLocales(LocaleList(locale))
        appResources.updateConfiguration(appConfig, appResources.displayMetrics)
    }
}