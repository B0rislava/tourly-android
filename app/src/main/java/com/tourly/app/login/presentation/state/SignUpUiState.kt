package com.tourly.app.login.presentation.state

import com.tourly.app.login.domain.UserRole

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "",
    val role: UserRole = UserRole.TRAVELER,
    val agreedToTerms: Boolean = false,

    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val fullNameError: String? = null,
    val termsError: String? = null,
    val signUpError: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val showVerificationDialog: Boolean = false,
    
    val verificationCode: String = "",
    val verificationError: String? = null,
    val isVerifying: Boolean = false,
    val verificationSuccess: Boolean = false,
    
    val resendTimer: Int = 0,
    val canResend: Boolean = true,

    // Google Sign Up Role Selection
    val showRoleSelectionDialog: Boolean = false,
    val pendingGoogleIdToken: String? = null
)