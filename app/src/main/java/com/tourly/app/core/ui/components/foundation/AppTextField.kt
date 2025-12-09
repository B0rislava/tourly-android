package com.tourly.app.core.ui.components.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = label,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .height(height = 56.dp)
            .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(size = 18.dp)),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(size = 18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onTertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
            focusedContainerColor = MaterialTheme.colorScheme.tertiary,
            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onTertiary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onTertiary,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onTertiary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onTertiary,
            focusedLabelColor = MaterialTheme.colorScheme.onTertiary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
        )
    )
}
