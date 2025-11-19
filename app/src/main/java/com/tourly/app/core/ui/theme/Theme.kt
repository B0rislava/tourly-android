package com.tourly.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

fun ThemeColors.toColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) {
        darkColorScheme(
            primary = primary,
            background = background,
            surface = surface,
            onPrimary = text,
            onBackground = text,
            onSurface = text,
            secondary = accent,
            onSecondary = background
        )
    } else {
        lightColorScheme(
            primary = primary,
            background = background,
            surface = surface,
            onPrimary = text,
            onBackground = text,
            onSurface = text,
            secondary = accent,
            onSecondary = background
        )
    }
}

@Composable
fun TourlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) ThemeColors.DarkColors else ThemeColors.LightColors

    val colorScheme = palette.toColorScheme(darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}