package com.tourly.app.core.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

fun ThemeColors.toColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) {
        darkColorScheme(
            background = background,
            onBackground = text,

            surface = surface,
            onSurface = primary,

            primary = primary,
            onPrimary = background,

            secondary = accent,
            onSecondary = background,

            error = error,
            onError = background
        )
    } else {
        lightColorScheme(
            background = background,
            onBackground = text,

            surface = surface,
            onSurface = primary,

            primary = primary,
            onPrimary = background,

            secondary = accent,
            onSecondary = background,

            error = error,
            onError = background
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}