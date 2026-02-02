package com.tourly.app.core.domain.repository

import com.tourly.app.core.domain.model.AppLanguage
import kotlinx.coroutines.flow.StateFlow

interface LanguageRepository {
    val currentLanguage: StateFlow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)
}
