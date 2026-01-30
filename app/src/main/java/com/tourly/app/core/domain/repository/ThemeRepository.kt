package com.tourly.app.core.domain.repository

import kotlinx.coroutines.flow.StateFlow

import com.tourly.app.core.domain.model.ThemeMode

interface ThemeRepository {
    val themeMode: StateFlow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
