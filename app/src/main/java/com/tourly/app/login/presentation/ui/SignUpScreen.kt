package com.tourly.app.login.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.presentation.ui.components.VerificationCodeDialog
import com.tourly.app.login.presentation.ui.components.GoogleRoleDialog
import com.tourly.app.login.presentation.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToSignIn: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    

    // Handle verification success
    LaunchedEffect(uiState.verificationSuccess) {
        if (uiState.verificationSuccess) {
            viewModel.onVerificationSuccessConsumed()
            onSignUpSuccess()
        }
    }

    // Handle Google Sign-Up success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.onSuccessConsumed()
            onSignUpSuccess()
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

    LaunchedEffect(uiState.signUpError) {
        uiState.signUpError?.let { errorMsg ->
            snackbarHostState.showSnackbar(errorMsg)
        }
    }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        SignUpContent(
            email = uiState.email,
            onEmailChange = viewModel::onEmailChange,
            password = uiState.password,
            onPasswordChange = viewModel::onPasswordChange,
            confirmPassword = uiState.confirmPassword,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            fullName = uiState.fullName,
            onFullNameChange = viewModel::onFullNameChange,
            role = uiState.role,
            onRoleChange = viewModel::onRoleChange,
            emailError = uiState.emailError?.let { stringResource(it) },
            passwordError = uiState.passwordError?.let { stringResource(it) },
            confirmPasswordError = uiState.confirmPasswordError?.let { stringResource(it) },
            fullNameError = uiState.fullNameError?.let { stringResource(it) },
            isLoading = uiState.isLoading,
            onRegisterClick = viewModel::signUp,
            onGoogleRegisterClick = { viewModel.googleSignUp(context) },
            onLoginClick = onNavigateToSignIn
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        )
    }
}

@Preview(
    name = "Sign Up Light Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Sign Up Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewSignUpScreen() {
    TourlyTheme {
        SignUpContent(
            email = "test@example.com",
            onEmailChange = {},
            password = "password123",
            onPasswordChange = {},
            confirmPassword = "password123",
            onConfirmPasswordChange = {},
            fullName = "John Doe",
            onFullNameChange = {},
            role = UserRole.GUIDE,
            onRoleChange = {},
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            fullNameError = null,
            isLoading = false,
            onLoginClick = {},
            onRegisterClick = {},
            onGoogleRegisterClick = {}
        )
    }
}