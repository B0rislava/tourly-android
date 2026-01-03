package com.tourly.app.core.presentation.ui.theme

import androidx.compose.ui.graphics.Color

sealed class ThemeColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val accent: Color,
    val text: Color,
    val error: Color
) {

    object LightColors : ThemeColors(
        background = Color(0xFFFAF7FF),
        surface = Color(0xFFF5ECFD),
        primary = Color(0xFFF18F66),
        accent = Color(0xFF9B6B9E),
        text = Color(0xFF3D2645),
        error = Color(0xFFDB211C)
    )

    object DarkColors : ThemeColors(
        background = Color(0xFF1A0E2E),
        surface = Color(0xFF2D1B3D),
        primary = Color(0xFFC77A5B),
        accent = Color(0xFFC89FCA),
        text = Color(0xFFFFF5ED),
        error = Color(0xFFFF6B6B),
    )
}
