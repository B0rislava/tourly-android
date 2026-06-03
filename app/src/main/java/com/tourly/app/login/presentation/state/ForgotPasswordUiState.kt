package com.tourly.app.login.presentation.state

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: Int? = null,

    // Phase 1: sending OTP
    val isLoading: Boolean = false,
    val sendError: String? = null,

    // Phase 2: OTP dialog
    val showVerificationDialog: Boolean = false,
    val verificationCode: String = "",
    val verificationError: String? = null,
    val isVerifying: Boolean = false,
    val verificationSuccess: Boolean = false,

    // Resend timer
    val canResend: Boolean = false,
    val resendTimer: Int = 60
)
