package com.tourly.app.login.presentation.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.presentation.ui.theme.TourlyTheme
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.presentation.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToSignIn: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle success navigation
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignUpSuccess()
        }
    }

    SignUpContent(
        email = uiState.email,
        onEmailChange = viewModel::onEmailChange,
        password = uiState.password,
        onPasswordChange = viewModel::onPasswordChange,
        firstName = uiState.firstName,
        onFirstNameChange = viewModel::onFirstNameChange,
        lastName = uiState.lastName,
        onLastNameChange = viewModel::onLastNameChange,
        role = uiState.role,
        onRoleChange = viewModel::onRoleChange,
        emailError = uiState.emailError,
        passwordError = uiState.passwordError,
        firstNameError = uiState.firstNameError,
        lastNameError = uiState.lastNameError,
        signUpError = uiState.signUpError,
        isLoading = uiState.isLoading,
        onRegisterClick = viewModel::signUp,
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
            firstName = "John",
            onFirstNameChange = {},
            lastName = "Doe",
            onLastNameChange = {},
            role = UserRole.GUIDE,
            onRoleChange = {},
            emailError = null,
            passwordError = null,
            firstNameError = null,
            lastNameError = null,
            signUpError = null,
            isLoading = false,
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}