package com.tourly.app.login.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.components.foundation.AppTextField

@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = stringResource(id = R.string.email),
    placeholder: String? = null,
    isError: Boolean = false,
    errorText: String? = null
){
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        isError = isError,
        errorText = errorText,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            autoCorrectEnabled = false,
            imeAction = ImeAction.Next
        )
    )
}