package com.tourly.app.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.login.domain.usecase.SignInUseCase
import com.tourly.app.login.presentation.state.SignInUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    // functions that the ui would call for some kind of user action for example a button click

    private val _uiState = MutableStateFlow(value = SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

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
                password = password, //FIXED!
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

            val result = signInUseCase(currentState.email, currentState.password)

            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        loginError = error.message ?: "Login failed"
                    )
                }
            }
        }
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