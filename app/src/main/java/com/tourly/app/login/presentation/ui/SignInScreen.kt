package com.tourly.app.login.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.presentation.ui.theme.TourlyTheme
import com.tourly.app.login.presentation.ui.components.VerificationCodeDialog
import com.tourly.app.login.presentation.ui.components.GoogleRoleDialog
import com.tourly.app.login.presentation.viewmodel.SignInViewModel


@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    // Handle verification success from login screen
    LaunchedEffect(uiState.verificationSuccess) {
        if (uiState.verificationSuccess) {
            onLoginSuccess()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
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

    if (uiState.showRoleSelectionDialog) {
        GoogleRoleDialog(
            onRoleSelected = viewModel::onRoleSelected,
            onDismiss = viewModel::closeRoleSelectionDialog
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.loginError) {
        uiState.loginError?.let { errorMsg ->
            snackbarHostState.showSnackbar(errorMsg)
        }
    }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        SignInContent(
            email = uiState.email,
            onEmailChange = viewModel::onEmailChange,
            password = uiState.password,
            onPasswordChange = viewModel::onPasswordChange,
            emailError = uiState.emailError?.let { stringResource(it) },
            passwordError = uiState.passwordError?.let { stringResource(it) },
            loginError = uiState.loginError,
            isLoading = uiState.isLoading,
            onLoginClick = viewModel::login,
            onRegisterClick = onNavigateToSignUp,
            onGoogleLoginClick = { viewModel.googleLogin(context) }
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(name = "Sign In Light Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(name = "Sign In Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewSignInScreen() {
    TourlyTheme {
        SignInContent(
            email = "test@example.com",
            onEmailChange = {},
            password = "password123",
            onPasswordChange = {},
            emailError = null,
            passwordError = null,
            loginError = null,
            isLoading = false,
            onLoginClick = {},
            onRegisterClick = {},
            onGoogleLoginClick = {}
        )
    }
}
