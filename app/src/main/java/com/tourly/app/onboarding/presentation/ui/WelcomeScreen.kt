package com.tourly.app.onboarding.presentation.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tourly.app.core.ui.theme.TourlyTheme

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit = {}
) {
    WelcomeContent(
        onGetStartedClick = onGetStartedClick
    )
}

@Preview(
    name = "Welcome Light Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Welcome Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewWelcomeScreen() {
    TourlyTheme {
        WelcomeContent(
            onGetStartedClick = {}
        )
    }
}