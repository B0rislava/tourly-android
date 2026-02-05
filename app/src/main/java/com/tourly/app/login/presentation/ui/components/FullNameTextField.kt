package com.tourly.app.login.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.components.foundation.AppTextField

@Composable
fun FullNameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = stringResource(id = R.string.full_name),
    placeholder: String? = stringResource(id = R.string.enter_full_name),
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
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = label
            )
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            autoCorrectEnabled = false,
            imeAction = ImeAction.Next
        )
    )
}
