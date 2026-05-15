package com.example.smartpick.features.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class ThemeRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        // Định nghĩa key bên trong companion object để đảm bảo tính duy nhất
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    /**
     * Lấy trạng thái Theme hiện tại.
     * Thêm catch để xử lý lỗi đọc file (IOException) thường gặp khi dùng DataStore.
     */
    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Sửa lỗi 'Return type mismatch' bằng cách dùng đúng thư viện Preferences
            preferences[IS_DARK_MODE] ?: false
        }

    /**
     * Cập nhật trạng thái Theme.
     */
    suspend fun toggleTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }
}