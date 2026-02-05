package com.tourly.app.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.R
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.presentation.state.SignInUiState
import com.tourly.app.core.network.Result
import com.tourly.app.login.domain.usecase.SignInUseCase
import com.tourly.app.login.domain.usecase.VerifyCodeUseCase
import com.tourly.app.login.domain.usecase.ResendCodeUseCase
import com.tourly.app.login.domain.usecase.GoogleSignInUseCase
import com.tourly.app.core.auth.GoogleAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val resendCodeUseCase: ResendCodeUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(value = SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = SignInUiState()
    }

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
                            loginError = result.message,
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

    fun login() {
        if (!validateCredentials()) return

        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loginError = null
                )
            }

            when (val result = signInUseCase(currentState.email, currentState.password)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }
                is Result.Error -> {
                    // Check if error is due to unverified email
                    if (result.code == "TY-8") {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                showVerificationDialog = true,
                                canResend = false,
                                resendTimer = 60
                            )
                        }
                        startResendTimer()
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                loginError = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun googleLogin() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loginError = null) }
            
            val idToken = googleAuthManager.getGoogleIdToken()
            
            if (idToken != null) {
                // First attempt without role to see if user exists
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
                        // Check for the specific "user not registered" error message
                        // Note: We need to make sure the Result object contains the right code
                        if (result.code == "TY-7") {
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
                                    loginError = result.message
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

    fun onVerificationCodeChange(code: String) {
        if (code.length <= 6) {
            _uiState.update { it.copy(verificationCode = code, verificationError = null) }
            if (code.length == 6) {
                verifyCode()
            }
        }
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

    private fun validateCredentials(): Boolean {
        var isValid = true
        val state = _uiState.value

        var emailError: Int? = null
        var passwordError: Int? = null

        if (state.email.isBlank()) {
            emailError = R.string.error_email_empty
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailError = R.string.error_email_invalid
            isValid = false
        }

        if (state.password.isBlank()) {
            passwordError = R.string.error_password_empty
            isValid = false
        }

        if (state.password.length < 6) {
            passwordError = R.string.error_password_too_short
            isValid = false
        }

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError
            )
        }

        return isValid
    }
}