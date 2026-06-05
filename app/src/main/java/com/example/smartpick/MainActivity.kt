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
import androidx.lifecycle.lifecycleScope
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.notification.data.NotificationRepository
import com.example.smartpick.features.settings.data.LanguageRepository
import com.example.smartpick.features.settings.viewmodel.SettingsViewModel
import com.example.smartpick.navigation.AppNavigation
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var languageRepository: LanguageRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lắng nghe trạng thái Session để tự động cập nhật Token khi User đăng nhập
        observeAuthSessionAndSyncToken()

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()

            val isDarkMode by settingsViewModel.isDarkMode.collectAsState(initial = false)
            val currentLanguage by settingsViewModel.currentLanguage.collectAsState(initial = "vi")

            LaunchedEffect(currentLanguage) {
                setAppLocale(currentLanguage)
            }

            SmartPickTheme(darkTheme = isDarkMode) {
                AppNavigation()
            }
        }
    }

    /**
     * Lắng nghe trạng thái Auth. Nếu có User hợp lệ thì lấy FCM Token và push lên DB.
     */
    private fun observeAuthSessionAndSyncToken() {
        lifecycleScope.launch {
            authRepository.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    val currentUser = authRepository.getCurrentUser()
                    currentUser?.id?.let { userId ->
                        if (userId.isNotEmpty()) {
                            syncFcmTokenToDatabase(userId)
                        }
                    }
                }
            }
        }
    }

    /**
     * Lấy token từ Firebase Messaging và gọi repository để lưu lên Supabase
     */
    private fun syncFcmTokenToDatabase(userId: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_MAIN", "Lấy FCM Token thất bại", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM_MAIN", "Đã lấy FCM Token thành công: $token")

            // Đẩy dữ liệu bất đồng bộ lên Supabase bằng Repository đã có
            lifecycleScope.launch {
                notificationRepository.upsertPushToken(token = token, userId = userId)
            }
        }
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = this.resources
        val configuration = resources.configuration

        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))
        resources.updateConfiguration(configuration, resources.displayMetrics)

        val appResources = applicationContext.resources
        val appConfig = appResources.configuration
        appConfig.setLocale(locale)
        appConfig.setLocales(LocaleList(locale))
        appResources.updateConfiguration(appConfig, appResources.displayMetrics)
    }
}