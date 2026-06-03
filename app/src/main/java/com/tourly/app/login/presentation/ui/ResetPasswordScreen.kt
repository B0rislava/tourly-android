package com.tourly.app.login.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.login.presentation.viewmodel.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(
    email: String,
    resetCode: String,
    viewModel: ResetPasswordViewModel = hiltViewModel(),
    onResetSuccess: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onResetSuccess()
        }
    }

    LaunchedEffect(uiState.resetError) {
        uiState.resetError?.let { snackbarHostState.showSnackbar(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ResetPasswordContent(
            newPassword = uiState.newPassword,
            onNewPasswordChange = viewModel::onNewPasswordChange,
            confirmPassword = uiState.confirmPassword,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            newPasswordError = uiState.newPasswordError?.let { stringResource(it) },
            confirmPasswordError = uiState.confirmPasswordError?.let { stringResource(it) },
            isLoading = uiState.isLoading,
            resetError = uiState.resetError,
            onResetClick = { viewModel.resetPassword(email, resetCode) },
            onBackToLoginClick = onBackToLoginClick
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        )
    }
}
