package com.tourly.app.create_tour.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun PricingRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontFamily = OutfitFamily, modifier = Modifier.weight(1f))
        Text("$", fontFamily = OutfitFamily, color = Color.Gray, modifier = Modifier.padding(end = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("0.00", color = Color.Gray) },
            modifier = Modifier.width(100.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFFE89E74)
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = androidx.compose.ui.text.style.TextAlign.End),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
    }
}