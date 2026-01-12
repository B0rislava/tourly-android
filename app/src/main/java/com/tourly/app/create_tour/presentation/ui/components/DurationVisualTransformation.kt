package com.tourly.app.create_tour.presentation.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DurationVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digitsOnly = text.text.filter { it.isDigit() }

        val formatted = when (digitsOnly.length) {
            0 -> ""
            1, 2 -> digitsOnly
            3 -> "${digitsOnly.take(2)}:${digitsOnly.substring(2)}"
            4 -> "${digitsOnly.take(2)}:${digitsOnly.substring(2)}"
            else -> digitsOnly
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Map original position to transformed position
                return when {
                    offset <= 2 -> offset
                    else -> offset + 1 // Account for the colon after position 2
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Map transformed position back to original
                return when {
                    offset <= 2 -> offset
                    offset == 3 -> 2 // The colon position maps back to position 2
                    else -> offset - 1 // Remove the colon offset
                }
            }
        }

        return TransformedText(androidx.compose.ui.text.AnnotatedString(formatted), offsetMapping)
    }
}