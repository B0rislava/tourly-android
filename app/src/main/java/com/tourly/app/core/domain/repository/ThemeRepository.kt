package com.tourly.app.core.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {
    val isDarkTheme: StateFlow<Boolean>
    suspend fun toggleTheme()
    suspend fun setDarkTheme(isDark: Boolean)
}
