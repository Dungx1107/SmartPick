package com.example.smartpick.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.features.auth.data.AuthRepository
import com.example.smartpick.features.settings.data.ThemeRepository
import com.example.smartpick.features.settings.data.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val languageRepository: LanguageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Quan sát trạng thái theme từ DataStore
    val isDarkMode: StateFlow<Boolean> = themeRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Quan sát trạng thái ngôn ngữ từ DataStore
    val currentLanguage: StateFlow<String> = languageRepository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "vi")

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            themeRepository.toggleTheme(isDark)
        }
    }

    fun setLanguage(languageCode: String) {
        viewModelScope.launch {
            languageRepository.setLanguage(languageCode)
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onSuccess()
        }
    }
}