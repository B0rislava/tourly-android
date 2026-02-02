package com.tourly.app.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

@Composable
fun DashboardIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = color
    )
}
