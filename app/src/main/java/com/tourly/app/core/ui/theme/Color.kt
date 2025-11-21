package com.tourly.app.core.ui.theme

import androidx.compose.ui.graphics.Color

sealed class ThemeColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val accent: Color,
    val text: Color
) {

    object LightColors : ThemeColors(
        background = Color(0xFFFFFAF2),
        surface = Color(0xFFF9E7CC),
        primary = Color(0xFFB1BC8D),
        accent = Color(0xFFD4A373),
        text = Color(0xFF755C4F)
    )

    object DarkColors : ThemeColors(
        background = Color(0xFF2D2620),
        surface = Color(0xFF473B35),
        primary = Color(0xFF747C68),
        accent = Color(0xFFD4A079),
        text = Color(0xFFF4ECE4)
    )
}
