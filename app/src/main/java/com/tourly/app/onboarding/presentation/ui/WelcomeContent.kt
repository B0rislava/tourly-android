package com.tourly.app.onboarding.presentation.ui

import com.tourly.app.core.presentation.ui.utils.WindowSizeState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.components.foundation.AppLogoWithText
import com.tourly.app.core.presentation.ui.components.foundation.OutlinedPillButton
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun WelcomeContent(
    windowSizeState: WindowSizeState,
    onGetStartedClick: () -> Unit,
    onTestConnectionClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        // Simplified: Only check for compact height and width
        val isCompactHeight = windowSizeState.isHeightCompact
        val isExpandedWidth = windowSizeState.isWidthExpanded

        WelcomeContentLayout(
            isCompactHeight = isCompactHeight,
            isExpandedWidth = isExpandedWidth,
            onGetStartedClick = onGetStartedClick,
            onTestConnectionClick = onTestConnectionClick
        )
    }
}

@Composable
private fun WelcomeContentLayout(
    isCompactHeight: Boolean,
    isExpandedWidth: Boolean,
    onGetStartedClick: () -> Unit,
    onTestConnectionClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (isCompactHeight) 40.dp else 60.dp))

            // Use Material 3 typography scale instead of manual sizing
            AppLogoWithText(fontSize = if (isCompactHeight) 32.sp else 40.sp)

            Spacer(modifier = Modifier.height(if (isCompactHeight) 12.dp else 16.dp))

            Text(
                text = stringResource(id = R.string.welcome_abroad),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(if (isCompactHeight) 4.dp else 8.dp))

            Text(
                text = stringResource(id = R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(if (isCompactHeight) 20.dp else 28.dp))

            OutlinedPillButton(
                text = "Start Now",
                onClick = onGetStartedClick,
                modifier = Modifier.wrapContentWidth()
            )
            
            Spacer(modifier = Modifier.height(height = 12.dp))
            
            OutlinedPillButton(
                text = "Test Connection",
                onClick = onTestConnectionClick,
                modifier = Modifier.wrapContentWidth()
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // Use fillMaxWidth with responsive fraction based on device type
        Image(
            painter = painterResource(id = R.drawable.traveler),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(if (isExpandedWidth) 0.6f else 0.85f),
            contentScale = ContentScale.FillWidth
        )
    }
}