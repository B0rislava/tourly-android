package com.tourly.app.core.ui.theme

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
        background = Color(0xFFFFF7F7),
        surface = Color(0xFF37274D),
        primary = Color(0xFFFFBEAF),
        accent = Color(0xFF526CB9),
        text = Color(0xFF37274D),
        error = Color(0xFFDB211C)
    )

    object DarkColors : ThemeColors(
        background = Color(0xFF1E162B),
        surface = Color(0xFF533E74),
        primary = Color(0xFFCDA3B9),
        accent = Color(0xFF526CB9),
        text = Color(0xFFF4F0FA),
        error = Color(0xFFE05959),
    )
}
