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
import com.tourly.app.login.presentation.viewmodel.SignInViewModel


@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    SignInContent(
        email = uiState.email,
        onEmailChange = viewModel::onEmailChange,
        password = uiState.password,
        onPasswordChange = viewModel::onPasswordChange,
        emailError = uiState.emailError,
        passwordError = uiState.passwordError,
        loginError = uiState.loginError,
        isLoading = uiState.isLoading,
        onLoginClick = viewModel::login,
        onRegisterClick = onNavigateToSignUp

    )

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
            onRegisterClick = {}
        )
    }
}
