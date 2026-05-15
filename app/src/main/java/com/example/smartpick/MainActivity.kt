package com.example.smartpick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.settings.viewmodel.SettingsViewModel
import com.example.smartpick.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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
