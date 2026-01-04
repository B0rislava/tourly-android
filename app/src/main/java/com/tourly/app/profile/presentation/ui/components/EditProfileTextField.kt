package com.tourly.app.profile.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun EditProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, fontFamily = OutfitFamily) },
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        supportingText = { supportingText?.let { Text(text = it, fontFamily = OutfitFamily) } },
        singleLine = true,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}