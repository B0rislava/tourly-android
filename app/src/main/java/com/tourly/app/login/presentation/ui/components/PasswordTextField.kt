package com.tourly.app.login.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.tourly.app.R
import com.tourly.app.core.ui.components.foundation.AppTextField

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = stringResource(id = R.string.password),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = stringResource(id = R.string.password)
            )
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Outlined.Visibility
                    else Icons.Outlined.VisibilityOff,
                    contentDescription = if (passwordVisible) stringResource(id = R.string.hide_password)
                    else stringResource(id = R.string.show_password)
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation()
    )
}
