package com.tourly.app.core.ui.theme

import androidx.compose.ui.graphics.Color

sealed class ThemeColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val accent: Color,
    val text: Color,
    val field: Color,
) {

    object LightColors : ThemeColors(
        primary = Color(0xFFB1BC8D),
        background = Color(0xFFFFFAF2),
        surface = Color(0xFFF9E7CC),
        accent = Color(0xFFD4A373),
        text = Color(0xFF755C4F),
        field = Color(0xFFFFFFFF),
    )

    object DarkColors : ThemeColors(
        primary = Color(0xFF747C68),
        background = Color(0xFF2D2620),
        surface = Color(0xFF473B35),
        accent = Color(0xFFD4A079),
        text = Color(0xFFF4ECE4),
        field = Color(0xFF23201D),
    )
}
