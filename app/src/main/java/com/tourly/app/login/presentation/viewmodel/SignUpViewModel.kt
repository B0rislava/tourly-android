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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val resendCodeUseCase: ResendCodeUseCase
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

    fun onFirstNameChange(firstName: String) {
        _uiState.update {
            it.copy(
                firstName = firstName,
                firstNameError = null
            )
        }
    }

    fun onLastNameChange(lastName: String) {
        _uiState.update {
            it.copy(
                lastName = lastName,
                lastNameError = null
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


            when (val result = signUpUseCase(
                email = currentState.email,
                password = currentState.password,
                firstName = currentState.firstName,
                lastName = currentState.lastName,
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
        var firstNameError: String? = null
        var lastNameError: String? = null

        // Validate first name
        if (state.firstName.isBlank()) {
            firstNameError = "First name cannot be empty"
            isValid = false
        } else if (state.firstName.length < 2) {
            firstNameError = "First name must be at least 2 characters"
            isValid = false
        }

        // Validate last name
        if (state.lastName.isBlank()) {
            lastNameError = "Last name cannot be empty"
            isValid = false
        } else if (state.lastName.length < 2) {
            lastNameError = "Last name must be at least 2 characters"
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

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                firstNameError = firstNameError,
                lastNameError = lastNameError
            )
        }

        return isValid
    }
}