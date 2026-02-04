package com.tourly.app.settings.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.profile.presentation.ui.components.EditProfileTextField
import com.tourly.app.profile.presentation.state.EditProfileUiState

@Composable
fun ChangePasswordContent(
    state: EditProfileUiState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Enter a new password", fontFamily = OutfitFamily, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))

        // Password
        EditProfileTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = "New Password",
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { stringResource(it) },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                 val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                 IconButton(onClick = { passwordVisible = !passwordVisible }) {
                     Icon(imageVector = image, contentDescription = null)
                 }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password
        EditProfileTextField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirm New Password",
            isError = state.confirmPasswordError != null,
            supportingText = state.confirmPasswordError?.let { stringResource(it) },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                 val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                 IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                     Icon(imageVector = image, contentDescription = null)
                 }
            }
        )

        if (state.saveError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(state.saveError, color = MaterialTheme.colorScheme.error, fontFamily = OutfitFamily)
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onSaveClick,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isSaving) {
                 CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                 Text("Save Password", fontFamily = OutfitFamily)
            }
        }
    }
}
