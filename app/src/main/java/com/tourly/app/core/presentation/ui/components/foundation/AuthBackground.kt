package com.tourly.app.core.presentation.ui.components.foundation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance

@Composable
fun AuthBackground(modifier: Modifier = Modifier) {
    // Detect dark theme based on the actual background color (respects app settings)
    val backgroundColor = MaterialTheme.colorScheme.background
    val isDarkTheme = backgroundColor.luminance() < 0.5f

    // Colors
    val sunsetPurple = Color(0xFFDC4EC5).copy(alpha = if (isDarkTheme) 0.15f else 0.25f)
    val sunsetPeach = Color(0xFFF59466).copy(alpha = if (isDarkTheme) 0.15f else 0.25f)
    val sunsetOrange = Color(0xFFE2762F).copy(alpha = if (isDarkTheme) 0.15f else 0.25f)

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw base background
        drawRect(color = backgroundColor)


        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(sunsetPurple, Color.Transparent),
                center = Offset(size.width * 0.9f, size.height * 0.05f),
                radius = size.width * 0.9f
            ),
            center = Offset(size.width * 0.9f, size.height * 0.05f),
            radius = size.width * 0.9f
        )


        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(sunsetOrange, Color.Transparent),
                center = Offset(size.width * 0.05f, size.height * 0.45f),
                radius = size.width * 0.7f
            ),
            center = Offset(size.width * 0.05f, size.height * 0.45f),
            radius = size.width * 0.7f
        )


        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(sunsetPeach, Color.Transparent),
                center = Offset(size.width * 0.85f, size.height * 0.85f),
                radius = size.width * 1.0f
            ),
            center = Offset(size.width * 0.85f, size.height * 0.85f),
            radius = size.width * 1.0f
        )
    }
}
