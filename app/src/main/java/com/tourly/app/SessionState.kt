package com.tourly.app

import com.tourly.app.login.domain.UserRole

sealed interface SessionState {
    data object Loading : SessionState
    data class Success(val isLoggedIn: Boolean, val userRole: UserRole?) : SessionState
}
