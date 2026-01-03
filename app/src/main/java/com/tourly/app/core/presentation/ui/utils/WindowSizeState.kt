package com.tourly.app.core.presentation.ui.utils

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

data class WindowSizeState(
    val isWidthCompact: Boolean,
    val isWidthMedium: Boolean,
    val isWidthExpanded: Boolean,
    val isHeightCompact: Boolean
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowSizeState(): WindowSizeState {
    val activity = LocalActivity.current ?: return WindowSizeState(
        isWidthCompact = true,
        isWidthMedium = false,
        isWidthExpanded = false,
        isHeightCompact = false
    )
    
    val windowSizeClass = calculateWindowSizeClass(activity)

    return remember(windowSizeClass) {
        WindowSizeState(
            isWidthCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact,
            isWidthMedium = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium,
            isWidthExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded,
            isHeightCompact = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
        )
    }
}
