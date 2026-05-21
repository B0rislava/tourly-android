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

    // Colors for the gradient
    val topOrange = Color(0xFFFF9A55).copy(alpha = if (isDarkTheme) 0.15f else 0.45f)
    val leftYellow = Color(0xFFFFD166).copy(alpha = if (isDarkTheme) 0.15f else 0.4f)
    val bottomPurple = Color(0xFFC785EC).copy(alpha = if (isDarkTheme) 0.15f else 0.45f)

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw base background
        drawRect(color = backgroundColor)

        // Top right orange
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(topOrange, Color.Transparent),
                center = Offset(size.width * 0.9f, size.height * 0.05f),
                radius = size.width * 0.9f
            ),
            center = Offset(size.width * 0.9f, size.height * 0.05f),
            radius = size.width * 0.9f
        )

        // Left middle yellow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(leftYellow, Color.Transparent),
                center = Offset(size.width * 0.0f, size.height * 0.4f),
                radius = size.width * 0.8f
            ),
            center = Offset(size.width * 0.0f, size.height * 0.4f),
            radius = size.width * 0.8f
        )

        // Bottom right purple
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(bottomPurple, Color.Transparent),
                center = Offset(size.width * 0.8f, size.height * 0.9f),
                radius = size.width * 1.0f
            ),
            center = Offset(size.width * 0.8f, size.height * 0.9f),
            radius = size.width * 1.0f
        )
    }
}
