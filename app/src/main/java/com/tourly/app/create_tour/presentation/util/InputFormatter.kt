package com.tourly.app.create_tour.presentation.util

import javax.inject.Inject

class InputFormatter @Inject constructor() {
    fun formatDuration(input: String): String {
        return input.filter { it.isDigit() }.take(4)
    }

    fun formatGroupSize(input: String): String {
        return input.filter { it.isDigit() }.take(3)
    }

    fun formatPrice(input: String): String {
        val filtered = input.filter { it.isDigit() || it == '.' }

        val decimalCount = filtered.count { it == '.' }
        if (decimalCount > 1) return input.dropLast(1)

        val parts = filtered.split(".")
        val formatted = if (parts.size == 2) {
            "${parts[0]}.${parts[1].take(2)}"
        } else {
            filtered
        }

        return if (formatted.length <= 8) formatted else input.dropLast(1)
    }
}