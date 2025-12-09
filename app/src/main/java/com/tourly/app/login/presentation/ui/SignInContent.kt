package com.tourly.app.login.presentation.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import com.tourly.app.login.presentation.ui.components.AuthCard
import com.tourly.app.login.presentation.ui.components.AuthCardHeader
import com.tourly.app.core.ui.components.foundation.ClickableText
import com.tourly.app.login.presentation.ui.components.EmailTextField
import com.tourly.app.login.presentation.ui.components.PasswordTextField
import com.tourly.app.core.ui.components.foundation.PrimaryButton

@Composable
fun SignInContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    AuthCard {
        AuthCardHeader(
            title = stringResource(id = R.string.welcome_back),
            subtitle = stringResource(id = R.string.adventure_awaits)
        )

        Spacer(modifier = Modifier.height(height = 20.dp))

        EmailTextField(
            value = email,
            onValueChange = onEmailChange
        )

        Spacer(modifier = Modifier.height(height = 10.dp))

        PasswordTextField(
            value = password,
            onValueChange = onPasswordChange
        )

        Spacer(modifier = Modifier.height(height = 20.dp))

        PrimaryButton(
            text = stringResource(id = R.string.login),
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.height(height = 8.dp))

        ClickableText(
            prefixText = stringResource(id = R.string.no_account),
            actionText = stringResource(id = R.string.register),
            onActionClick = onRegisterClick
        )
    }
}