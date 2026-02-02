package com.tourly.app.core.data.repository

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tourly.app.core.domain.model.AppLanguage
import com.tourly.app.core.domain.repository.LanguageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LanguageRepository {
    
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    override val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    init {
        // Load the saved language from DataStore on initialization
        runBlocking {
            val savedLanguageCode = dataStore.data.map { preferences ->
                preferences[LANGUAGE_KEY]
            }.first()
            
            val language = savedLanguageCode?.let { AppLanguage.fromCode(it) } ?: AppLanguage.ENGLISH
            _currentLanguage.value = language
            
            // Ensure AppCompatDelegate is in sync
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language.code)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    override suspend fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        
        // Save to DataStore
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.code
        }
        
        // Update AppCompatDelegate
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
    
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")
    }
}
