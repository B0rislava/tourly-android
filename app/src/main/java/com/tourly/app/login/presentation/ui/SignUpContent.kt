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
import com.tourly.app.login.presentation.ui.components.FirstNameTextField
import com.tourly.app.login.presentation.ui.components.LastNameTextField
import com.tourly.app.login.presentation.ui.components.PasswordTextField
import com.tourly.app.core.ui.components.foundation.PrimaryButton
import com.tourly.app.login.presentation.ui.components.RoleSelector
import com.tourly.app.login.presentation.ui.components.UserRole

@Composable
fun SignUpContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    role: UserRole,
    onRoleChange: (UserRole) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    AuthCard {
        AuthCardHeader(
            title = stringResource(id = R.string.welcome_abroad),
            subtitle = stringResource(id = R.string.journey_starts)
        )

        Spacer(modifier = Modifier.height(height = 20.dp))


        RoleSelector(
            selectedRole = role,
            onRoleSelected = onRoleChange
        )

        Spacer(modifier = Modifier.height(height = 10.dp))

        FirstNameTextField(
            value = firstName,
            onValueChange = onFirstNameChange
        )

        Spacer(modifier = Modifier.height(height = 10.dp))

        LastNameTextField(
            value = lastName,
            onValueChange = onLastNameChange
        )

        Spacer(modifier = Modifier.height(height = 10.dp))

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
            text = stringResource(id = R.string.register),
            onClick = onRegisterClick
        )

        Spacer(modifier = Modifier.height(height = 8.dp))

        ClickableText(
            prefixText = stringResource(id = R.string.have_account),
            actionText = stringResource(id = R.string.login),
            onActionClick = onLoginClick
        )
    }

}