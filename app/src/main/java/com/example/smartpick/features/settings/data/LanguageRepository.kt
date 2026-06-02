package com.example.smartpick.features.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    /**
     * Get the current stored language. Default is "vi" (Vietnamese).
     */
    val language: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "vi"
        }

    /**
     * Update the saved language.
     */
    suspend fun setLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }
}
