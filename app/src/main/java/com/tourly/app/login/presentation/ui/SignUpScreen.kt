package com.tourly.app.login.presentation.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    
    // Reset state on entry to prevent stale verification dialogs
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    // Handle verification success
    LaunchedEffect(uiState.verificationSuccess) {
        if (uiState.verificationSuccess) {
            onSignUpSuccess()
        }
    }

    // Handle Google Sign-Up success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
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
        agreedToTerms = uiState.agreedToTerms,
        onAgreeToTermsChange = viewModel::onAgreeToTermsChange,
        emailError = uiState.emailError,
        passwordError = uiState.passwordError,
        confirmPasswordError = uiState.confirmPasswordError,
        fullNameError = uiState.fullNameError,
        termsError = uiState.termsError,
        signUpError = uiState.signUpError,
        isLoading = uiState.isLoading,
        onRegisterClick = viewModel::signUp,
        onGoogleRegisterClick = viewModel::googleSignUp,
        onLoginClick = onNavigateToSignIn
    )
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
            agreedToTerms = true,
            onAgreeToTermsChange = {},
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            fullNameError = null,
            termsError = null,
            signUpError = null,
            isLoading = false,
            onLoginClick = {},
            onRegisterClick = {},
            onGoogleRegisterClick = {}
        )
    }
}