package com.tourly.app.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.login.presentation.state.SignInUiState
import com.tourly.app.core.network.Result
import com.tourly.app.login.domain.usecase.SignInUseCase
import com.tourly.app.login.domain.usecase.VerifyCodeUseCase
import com.tourly.app.login.domain.usecase.ResendCodeUseCase
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
    private val resendCodeUseCase: ResendCodeUseCase
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
                    if (result.message.contains("verify your email", ignoreCase = true)) {
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

        var emailError: String? = null
        var passwordError: String? = null

        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailError = "Invalid email"
            isValid = false
        }

        if (state.password.isBlank()) {
            passwordError = "Password cannot be empty"
            isValid = false
        }

        if (state.password.length < 6) {
            passwordError = "Password must be at least 6 characters"
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