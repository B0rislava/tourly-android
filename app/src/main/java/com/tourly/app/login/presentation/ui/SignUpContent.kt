package com.tourly.app.login.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.tourly.app.core.presentation.ui.components.foundation.ClickableText
import com.tourly.app.core.presentation.ui.components.foundation.PrimaryButton
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.presentation.ui.components.RoleSelector
import com.tourly.app.login.presentation.ui.components.FirstNameTextField
import com.tourly.app.login.presentation.ui.components.LastNameTextField
import com.tourly.app.login.presentation.ui.components.EmailTextField
import com.tourly.app.login.presentation.ui.components.PasswordTextField
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
    emailError: String?,
    passwordError: String?,
    firstNameError: String?,
    lastNameError: String?,
    signUpError: String?,
    isLoading: Boolean,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(90.dp))

            Text(
                text = stringResource(id = R.string.welcome_abroad),
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.journey_starts),
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            RoleSelector(
                selectedRole = role,
                onRoleSelected = onRoleChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            FirstNameTextField(
                value = firstName,
                onValueChange = onFirstNameChange
            )

            if (firstNameError != null) {
                Text(
                    text = firstNameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LastNameTextField(
                value = lastName,
                onValueChange = onLastNameChange
            )

            if (lastNameError != null) {
                Text(
                    text = lastNameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            EmailTextField(
                value = email,
                onValueChange = onEmailChange
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
                onValueChange = onPasswordChange
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

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = stringResource(id = R.string.register),
                onClick = onRegisterClick,
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

            if (signUpError != null) {
                Text(
                    text = signUpError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            ClickableText(
                prefixText = stringResource(id = R.string.have_account),
                actionText = stringResource(id = R.string.login),
                onActionClick = onLoginClick
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}