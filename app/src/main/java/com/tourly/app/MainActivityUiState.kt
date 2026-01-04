package com.tourly.app

import com.tourly.app.login.domain.UserRole

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(
        val isUserLoggedIn: Boolean,
        val userRole: UserRole? = null
    ) : MainActivityUiState
}
