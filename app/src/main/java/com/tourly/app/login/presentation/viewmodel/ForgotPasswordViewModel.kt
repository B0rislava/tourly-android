package com.tourly.app.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.R
import com.tourly.app.core.network.Result
import com.tourly.app.login.domain.usecase.ForgotPasswordUseCase
import com.tourly.app.login.domain.usecase.VerifyResetCodeUseCase
import com.tourly.app.login.domain.usecase.ResendCodeUseCase
import com.tourly.app.login.presentation.state.ForgotPasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val verifyResetCodeUseCase: VerifyResetCodeUseCase,
    private val resendCodeUseCase: ResendCodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, sendError = null) }
    }

    fun sendResetEmail() {
        if (!validateEmail()) return

        val email = _uiState.value.email

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, sendError = null) }

            when (val result = forgotPasswordUseCase(email)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showVerificationDialog = true,
                            canResend = false,
                            resendTimer = 60
                        )
                    }
                    startResendTimer()
                }
                is Result.Error -> {
                    if (result.code == "TY-9") {
                        _uiState.update {
                            it.copy(isLoading = false, sendErrorRes = R.string.error_google_account_no_password, sendError = null)
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, sendError = result.message, sendErrorRes = null) }
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
        val state = _uiState.value
        if (state.verificationCode.length != 6) return

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, verificationError = null) }

            when (val result = verifyResetCodeUseCase(state.email, state.verificationCode)) {
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
        val state = _uiState.value
        if (!state.canResend) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(canResend = false, resendTimer = 60, verificationError = null, verificationCode = "")
            }
            startResendTimer()

            when (val result = resendCodeUseCase(state.email)) {
                is Result.Error -> {
                    _uiState.update { it.copy(verificationError = result.message) }
                }
                else -> Unit
            }
        }
    }

    fun closeVerificationDialog() {
        _uiState.update {
            it.copy(
                showVerificationDialog = false,
                verificationCode = "",
                verificationError = null,
                verificationSuccess = false
            )
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

    private fun validateEmail(): Boolean {
        val email = _uiState.value.email
        return when {
            email.isBlank() -> {
                _uiState.update { it.copy(emailError = R.string.error_email_empty) }
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.update { it.copy(emailError = R.string.error_email_invalid) }
                false
            }
            else -> true
        }
    }
}
