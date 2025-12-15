package com.tourly.app.login.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tourly.app.R
import com.tourly.app.core.ui.components.foundation.AppTextField

@Composable
fun LastNameTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = "Last Name",
        placeholder = "Jane DOe",
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = stringResource(id = R.string.last_name)
            )
        }
    )
}

