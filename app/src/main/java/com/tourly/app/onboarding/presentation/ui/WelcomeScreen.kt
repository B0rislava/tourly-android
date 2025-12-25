package com.tourly.app.onboarding.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.tourly.app.core.ui.theme.TourlyTheme
import android.content.res.Configuration
import com.tourly.app.core.ui.utils.WindowSizeState

@Preview(
    name = "1. Small Phone (hdpi)",
    group = "Density",
    device = "spec:width=320dp,height=533dp,dpi=240"
)
@Preview(
    name = "2. Standard Phone (xxhdpi)",
    group = "Density",
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Preview(
    name = "3. High-End Phone (xxxhdpi)",
    group = "Density",
    device = "spec:width=384dp,height=850dp,dpi=560"
)
@Preview(
    name = "4. Tablet",
    group = "Density",
    device = Devices.PIXEL_C
)
@Preview(
    name = "5. Standard Phone - Dark",
    group = "Dark Mode",
    device = Devices.PIXEL_5,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class DevicePreviews

@Composable
fun WelcomeScreen(
    windowSizeState: WindowSizeState,
    onGetStartedClick: () -> Unit = {}
) {
    WelcomeContent(
        windowSizeState = windowSizeState,
        onGetStartedClick = onGetStartedClick
    )
}

@DevicePreviews
@Composable
fun PreviewWelcomeScreen() {
    val windowSizeState = WindowSizeState(
        isWidthCompact = true,
        isWidthMedium = false,
        isWidthExpanded = false,
        isHeightCompact = false
    )
    TourlyTheme {
        WelcomeContent(
            windowSizeState = windowSizeState,
            onGetStartedClick = {}
        )
    }
}