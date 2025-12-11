package com.tourly.app.login.presentation.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.tourly.app.core.ui.theme.TourlyTheme


@Composable
fun SignInScreen(
    // TODO: implement viewModel
    onNavigateToSignUp: () -> Unit = {}
) {
    var email by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value ="") }

    SignInContent(
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        onLoginClick = {
            println("Attempting login with Email: $email and Password: $password")
            // TODO: viewModel.login(email, password)
        },
        onRegisterClick = onNavigateToSignUp

    )

}

@Preview(name = "Sign In Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(name = "Sign In Light Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewSignInScreen() {
    TourlyTheme {
        SignInContent(
            email = "test@example.com",
            onEmailChange = {},
            password = "password123",
            onPasswordChange = {},
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}
