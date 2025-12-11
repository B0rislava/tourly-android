package com.tourly.app.login.presentation.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.tourly.app.login.presentation.ui.components.UserRole
import com.tourly.app.core.ui.theme.TourlyTheme

@Composable
fun SignUpScreen(
    // TODO: implement viewModel
    onNavigateToSignIn: () -> Unit = {}
) {

    var email by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var firstName by remember { mutableStateOf(value = "") }
    var lastName by remember { mutableStateOf(value = "") }
    var role by remember { mutableStateOf(value = UserRole.Traveler) }

    SignUpContent(
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        firstName = firstName,
        onFirstNameChange = { firstName = it },
        lastName = lastName,
        onLastNameChange = { lastName = it },
        role = role,
        onRoleChange = { role = it },
        onRegisterClick = {
            // TODO: viewModel.register(...)
        },
        onLoginClick = onNavigateToSignIn
    )
}

@Preview(name = "Sign Up Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(name = "Sign Up Light Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewSignUpScreen() {
    TourlyTheme {
        SignUpContent(
            email = "test@example.com",
            onEmailChange = {},
            password = "password123",
            onPasswordChange = {},
            firstName = "test",
            onFirstNameChange = {},
            lastName = "test",
            onLastNameChange = {},
            role = UserRole.Guide,
            onRoleChange = {},
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}
