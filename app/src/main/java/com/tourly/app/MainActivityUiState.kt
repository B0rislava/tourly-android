package com.tourly.app

import com.tourly.app.login.domain.UserRole
import com.tourly.app.core.domain.model.ThemeMode

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(
        val isUserLoggedIn: Boolean,
        val userRole: UserRole? = null,
        val themeMode: ThemeMode = ThemeMode.SYSTEM
    ) : MainActivityUiState
}
