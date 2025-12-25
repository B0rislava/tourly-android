package com.tourly.app.login.presentation.state

data class SignInUiState(
    val email: String = "",
    val password: String = "",

    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
)
