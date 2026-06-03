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
import com.tourly.app.login.presentation.ui.components.VerificationCodeDialog
import com.tourly.app.login.presentation.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onVerificationSuccess: (email: String, code: String) -> Unit,
    onBackToLoginClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigate to reset password once OTP is verified
    LaunchedEffect(uiState.verificationSuccess) {
        if (uiState.verificationSuccess) {
            onVerificationSuccess(uiState.email, uiState.verificationCode)
        }
    }

    LaunchedEffect(uiState.sendError) {
        uiState.sendError?.let { snackbarHostState.showSnackbar(it) }
    }

    if (uiState.showVerificationDialog) {
        VerificationCodeDialog(
            email = uiState.email,
            code = uiState.verificationCode,
            onCodeChange = viewModel::onVerificationCodeChange,
            onDismiss = viewModel::closeVerificationDialog,
            error = uiState.verificationError,
            isVerifying = uiState.isVerifying,
            isSuccess = uiState.verificationSuccess,
            onResend = viewModel::resendCode,
            canResend = uiState.canResend,
            resendTimer = uiState.resendTimer
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ForgotPasswordContent(
            email = uiState.email,
            onEmailChange = viewModel::onEmailChange,
            emailError = uiState.emailError?.let { stringResource(it) },
            isLoading = uiState.isLoading,
            sendError = uiState.sendError,
            onSendClick = viewModel::sendResetEmail,
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
