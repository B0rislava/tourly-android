package com.tourly.app.login.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.R
import com.tourly.app.core.network.Result
import com.tourly.app.login.domain.usecase.ResetPasswordUseCase
import com.tourly.app.login.presentation.state.ResetPasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password, newPasswordError = null, resetError = null) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password, confirmPasswordError = null, resetError = null) }
    }

    fun resetPassword(email: String, resetCode: String) {
        if (!validate()) return

        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, resetError = null) }

            when (val result = resetPasswordUseCase(email, resetCode, state.newPassword)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, resetError = result.message) }
                }
            }
        }
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        var isValid = true
        var newPasswordError: Int? = null
        var confirmPasswordError: Int? = null

        if (state.newPassword.isBlank()) {
            newPasswordError = R.string.error_password_empty
            isValid = false
        } else if (state.newPassword.length < 6) {
            newPasswordError = R.string.error_password_too_short
            isValid = false
        } else if (!state.newPassword.any { it.isDigit() }) {
            newPasswordError = R.string.error_password_no_digit
            isValid = false
        }

        if (state.confirmPassword != state.newPassword) {
            confirmPasswordError = R.string.error_passwords_dont_match
            isValid = false
        }

        _uiState.update {
            it.copy(
                newPasswordError = newPasswordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        return isValid
    }
}
