package com.tourly.app.login.presentation.state

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val showRoleSelectionDialog: Boolean = false,
    val pendingGoogleIdToken: String? = null,

    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,

    // Verification related fields
    val showVerificationDialog: Boolean = false,
    val verificationCode: String = "",
    val verificationError: String? = null,
    val isVerifying: Boolean = false,
    val verificationSuccess: Boolean = false,
    val resendTimer: Int = 0,
    val canResend: Boolean = true
)
