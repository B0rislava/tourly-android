package com.tourly.app.login.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import com.tourly.app.login.presentation.ui.components.AuthCardHeader
import com.tourly.app.core.presentation.ui.components.foundation.ClickableText
import com.tourly.app.login.presentation.ui.components.EmailTextField
import com.tourly.app.login.presentation.ui.components.PasswordTextField
import com.tourly.app.core.presentation.ui.components.foundation.PrimaryButton

@Composable
fun SignInContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    emailError: String?,
    passwordError: String?,
    loginError: String?,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(100.dp))

            AuthCardHeader(
                title = stringResource(id = R.string.welcome_back),
                subtitle = stringResource(id = R.string.adventure_awaits)
            )
            Spacer(modifier = Modifier.height(35.dp))

            EmailTextField(
                value = email,
                onValueChange = onEmailChange
            )

            if (emailError != null) {
                Text(
                    text = emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                value = password,
                onValueChange = onPasswordChange
            )

            if (passwordError != null) {
                Text(
                    text = passwordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(height = 20.dp))

            PrimaryButton(
                text = stringResource(id = R.string.login),
                onClick = onLoginClick,
                enabled = !isLoading,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if (loginError != null) {
                Text(
                    text = loginError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(height = 8.dp))

            ClickableText(
                prefixText = stringResource(id = R.string.no_account),
                actionText = stringResource(id = R.string.register),
                onActionClick = onRegisterClick
            )

            Spacer(modifier = Modifier.height(40.dp))

        }
    }
}