package com.tourly.app.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tourly.app.core.domain.model.ThemeMode
import com.tourly.app.core.domain.repository.ThemeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ThemeRepository {
    private val themeKey = stringPreferencesKey("theme_mode")
    
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    override val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val savedTheme = dataStore.data.map { prefs ->
                val themeName = prefs[themeKey] ?: ThemeMode.SYSTEM.name
                ThemeMode.valueOf(themeName)
            }.first()
            _themeMode.value = savedTheme
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[themeKey] = mode.name
        }
        _themeMode.value = mode
    }
}
