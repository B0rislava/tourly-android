package com.tourly.app.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.domain.usecase.SignUpUseCase
import com.tourly.app.login.domain.usecase.VerifyCodeUseCase
import com.tourly.app.login.presentation.state.SignUpUiState
import com.tourly.app.core.network.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.tourly.app.login.domain.usecase.ResendCodeUseCase
import com.tourly.app.login.domain.usecase.GoogleSignInUseCase
import com.tourly.app.core.auth.GoogleAuthManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val resendCodeUseCase: ResendCodeUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = null
            )
        }
    }

    fun onAgreeToTermsChange(agreed: Boolean) {
        _uiState.update {
            it.copy(
                agreedToTerms = agreed,
                termsError = null
            )
        }
    }

    fun onFullNameChange(fullName: String) {
        _uiState.update {
            it.copy(
                fullName = fullName,
                fullNameError = null
            )
        }
    }

    fun onRoleChange(role: UserRole) {
        _uiState.update { it.copy(role = role) }
    }

    fun resetState() {
        _uiState.value = SignUpUiState()
    }

    fun onVerificationCodeChange(code: String) {
        if (code.length <= 6) {
            _uiState.update { it.copy(verificationCode = code, verificationError = null) }
            if (code.length == 6) {
                verifyCode()
            }
        }
    }

    fun signUp() {
        if (!validateFields()) return

        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    signUpError = null
                )
            }


            val names = currentState.fullName.trim().split(" ", limit = 2)
            val firstName = names.getOrNull(0) ?: ""
            val lastName = names.getOrNull(1) ?: ""

            when (val result = signUpUseCase(
                email = currentState.email,
                password = currentState.password,
                firstName = firstName,
                lastName = lastName,
                role = currentState.role
            )) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            showVerificationDialog = true,
                            canResend = false,
                            resendTimer = 60
                        )
                    }
                    startResendTimer()
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            signUpError = result.message
                        )
                    }
                }
            }
        }
    }

    fun googleSignUp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, signUpError = null) }
            
            val idToken = googleAuthManager.getGoogleIdToken()
            
            if (idToken != null) {
                // Pass null initially to check if user exists or force role selection
                when (val result = googleSignInUseCase(idToken, null)) {
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                isSuccess = true
                            )
                        }
                    }
                    is Result.Error -> {
                        // If user is not found (meaning new user and no role provided), show dialog
                        if (result.message.contains("not registered", ignoreCase = true)) {
                             _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    showRoleSelectionDialog = true,
                                    pendingGoogleIdToken = idToken
                                )
                            }
                        } else {
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    signUpError = result.message
                                )
                            }
                        }
                    }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onRoleSelected(role: UserRole) {
        val idToken = _uiState.value.pendingGoogleIdToken ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showRoleSelectionDialog = false) }
            
            when (val result = googleSignInUseCase(idToken, role)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isSuccess = true,
                            pendingGoogleIdToken = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            signUpError = result.message,
                            pendingGoogleIdToken = null
                        )
                    }
                }
            }
        }
    }

    fun closeRoleSelectionDialog() {
        _uiState.update { it.copy(showRoleSelectionDialog = false, pendingGoogleIdToken = null) }
    }

    fun verifyCode() {
        val currentState = _uiState.value
        if (currentState.verificationCode.length != 6) return

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, verificationError = null) }

            when (val result = verifyCodeUseCase(
                email = currentState.email,
                code = currentState.verificationCode
            )) {
                is Result.Success -> {
                    _uiState.update { it.copy(isVerifying = false, verificationSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isVerifying = false, verificationError = result.message) }
                }
            }
        }
    }

    fun resendCode() {
        val currentState = _uiState.value
        if (!currentState.canResend) return

        viewModelScope.launch {
            _uiState.update { it.copy(canResend = false, resendTimer = 60, verificationError = null) }
            startResendTimer()

            when (val result = resendCodeUseCase(currentState.email)) {
                is Result.Success -> {
                    _uiState.update { it.copy(verificationCode = "") }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(verificationError = result.message) }
                }
            }
        }
    }

    private fun startResendTimer() {
        viewModelScope.launch {
            while (_uiState.value.resendTimer > 0) {
                delay(1000)
                _uiState.update { it.copy(resendTimer = it.resendTimer - 1) }
            }
            _uiState.update { it.copy(canResend = true) }
        }
    }

    fun closeVerificationDialog() {
        _uiState.update { it.copy(showVerificationDialog = false) }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        val state = _uiState.value

        var emailError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null
        var fullNameError: String? = null
        var termsError: String? = null

        // Validate full name
        if (state.fullName.isBlank()) {
            fullNameError = "Name cannot be empty"
            isValid = false
        } else if (!state.fullName.trim().contains(" ")) {
            fullNameError = "Please enter both first and last name"
            isValid = false
        }

        // Validate email
        if (state.email.isBlank()) {
            emailError = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailError = "Invalid email format"
            isValid = false
        }

        // Validate password
        if (state.password.isBlank()) {
            passwordError = "Password cannot be empty"
            isValid = false
        } else if (state.password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else if (!state.password.any { it.isDigit() }) {
            passwordError = "Password must contain at least one digit"
            isValid = false
        }

        // Validate confirm password
        if (state.confirmPassword != state.password) {
            confirmPasswordError = "Passwords do not match"
            isValid = false
        }

        // Validate terms
        if (!state.agreedToTerms) {
            termsError = "You must agree to the terms and conditions"
            isValid = false
        }

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                fullNameError = fullNameError,
                termsError = termsError
            )
        }

        return isValid
    }
}