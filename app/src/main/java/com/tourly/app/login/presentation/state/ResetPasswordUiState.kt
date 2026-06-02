package com.tourly.app.login.presentation.state

data class ResetPasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val newPasswordError: Int? = null,
    val confirmPasswordError: Int? = null,

    val isLoading: Boolean = false,
    val resetError: String? = null,
    val isSuccess: Boolean = false
)
