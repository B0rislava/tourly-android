package com.tourly.app.core.presentation.ui.components.foundation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AuthBackground(modifier: Modifier = Modifier) {

    val sunsetPurple = Color(0xFFB162A6).copy(alpha = 0.25f)
    val sunsetPeach = Color(0xFFF59466).copy(alpha = 0.25f)
    val sunsetOrange = Color(0xFFE2762F).copy(alpha = 0.25f)
    val white = Color(0xFFFFFFFF)

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw base white
        drawRect(color = white)


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
