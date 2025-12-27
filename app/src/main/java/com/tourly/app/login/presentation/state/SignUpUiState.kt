package com.tourly.app.login.presentation.state

import com.tourly.app.login.domain.UserRole

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val role: UserRole = UserRole.TRAVELER,

    val emailError: String? = null,
    val passwordError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val signUpError: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)