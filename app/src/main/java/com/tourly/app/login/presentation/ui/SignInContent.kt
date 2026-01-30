package com.tourly.app.login.presentation.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.core.presentation.ui.components.foundation.AuthBackground
import com.tourly.app.core.presentation.ui.components.foundation.ClickableText
import com.tourly.app.core.presentation.ui.components.foundation.PrimaryButton
import com.tourly.app.login.presentation.ui.components.EmailTextField
import com.tourly.app.login.presentation.ui.components.PasswordTextField
import com.tourly.app.login.presentation.ui.components.OrDivider
import com.tourly.app.login.presentation.ui.components.GoogleSignInButton

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
    onGoogleLoginClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AuthBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = stringResource(id = R.string.welcome_back),
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.adventure_awaits),
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            EmailTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(id = R.string.email),
                placeholder = stringResource(id = R.string.enter_email)
            )

            if (emailError != null) {
                Text(
                    text = emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(id = R.string.password),
                placeholder = stringResource(id = R.string.enter_password)
            )

            if (passwordError != null) {
                Text(
                    text = passwordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = stringResource(id = R.string.login),
                onClick = onLoginClick,
                enabled = !isLoading,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            if (loginError != null) {
                Text(
                    text = loginError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OrDivider()

            Spacer(modifier = Modifier.height(16.dp))

            GoogleSignInButton(
                onClick = onGoogleLoginClick,
                text = stringResource(id = R.string.google)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ClickableText(
                prefixText = stringResource(id = R.string.no_account),
                actionText = stringResource(id = R.string.register),
                onActionClick = onRegisterClick
            )

            Spacer(modifier = Modifier.height(40.dp))

        }
    }
}