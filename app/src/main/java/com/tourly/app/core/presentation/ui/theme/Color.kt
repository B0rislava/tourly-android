package com.tourly.app.core.presentation.ui.theme

import androidx.compose.ui.graphics.Color

sealed class ThemeColors(
    val background: Color,
    val surface: Color,

    val primary: Color,
    val secondary: Color,
    val tertiary: Color,

    val text: Color,
    val secondaryText: Color,

    val error: Color,

    val cardBackground: Color,
    val cardBorder: Color
) {

    object LightColors : ThemeColors(
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF5ECFD),

        primary = Color(0xFFF59466),
        secondary = Color(0xFFFFB789),
        tertiary = Color(0xFFB16DB4),

        text = Color(0xFF3D2645),
        secondaryText = Color(0xFFFFFFFF),

        error = Color(0xFFE55B4D),

        cardBackground = Color(0xFFF6F3FA),
        cardBorder = Color(0xFFF5ECFD),
    )

    object DarkColors : ThemeColors(
        background = Color(0xFF230E2E),
        surface = Color(0xFF381B3D),

        primary = Color(0xFFC7745B),
        secondary = Color(0xFFEAAC82),
        tertiary = Color(0xFFB885B0),

        text = Color(0xFFFFF5ED),
        secondaryText = Color(0xFFAC91BF),

        error = Color(0xFFFF6B6B),

        cardBackground = Color(0xFF3F204C),
        cardBorder = Color(0xFF673778),
    )
}
