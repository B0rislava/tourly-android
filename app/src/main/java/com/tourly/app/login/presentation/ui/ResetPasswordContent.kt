package com.tourly.app.login.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.tourly.app.core.presentation.ui.components.foundation.AuthBackground
import com.tourly.app.core.presentation.ui.components.foundation.ClickableText
import com.tourly.app.core.presentation.ui.components.foundation.PrimaryButton
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.login.presentation.ui.components.PasswordTextField

@Composable
fun ResetPasswordContent(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    newPasswordError: String?,
    confirmPasswordError: String?,
    isLoading: Boolean,
    resetError: String?,
    onResetClick: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AuthBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.reset_password_title),
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.reset_password_subtitle),
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            PasswordTextField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                placeholder = stringResource(R.string.enter_new_password),
                isError = newPasswordError != null,
                errorText = newPasswordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = stringResource(R.string.enter_confirm_password),
                isError = confirmPasswordError != null,
                errorText = confirmPasswordError
            )

            if (resetError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = resetError,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = OutfitFamily,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = if (isLoading) stringResource(R.string.resetting) else stringResource(R.string.reset_password),
                onClick = onResetClick,
                enabled = !isLoading,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            ClickableText(
                prefixText = stringResource(R.string.remember_password),
                actionText = stringResource(R.string.log_in),
                onActionClick = onBackToLoginClick
            )
        }
    }
}
