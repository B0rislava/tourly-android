package com.tourly.app.onboarding.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tourly.app.R
import com.tourly.app.core.ui.components.foundation.AppLogoWithText
import com.tourly.app.core.ui.components.foundation.OutlinedPillButton
import com.tourly.app.core.ui.theme.OutfitFamily

@Composable
fun WelcomeContent(
    onGetStartedClick: () -> Unit,
    onTestConnectionClick: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        val isTablet = screenWidth > 600.dp
        val isSmallScreen = screenHeight < 600.dp
        val isMediumScreen = screenHeight < 800.dp

        val topPadding = when {
            isSmallScreen -> 40.dp
            isMediumScreen -> 60.dp
            else -> 70.dp
        }

        val titleSize = when {
            isSmallScreen -> 28.sp
            isMediumScreen -> 34.sp
            else -> 38.sp
        }

        val subtitleSize = when {
            isSmallScreen -> 14.sp
            isMediumScreen -> 16.sp
            else -> 18.sp
        }

        val logoSize = when {
            isSmallScreen -> 32.sp
            isMediumScreen -> 36.sp
            else -> 40.sp
        }

        val imageWidth = when {
            isTablet -> 0.5f
            isSmallScreen -> 0.75f
            isMediumScreen -> 0.85f
            else -> 0.9f
        }

        val imageSpacing = when {
            isTablet -> 350.dp
            isSmallScreen -> 200.dp
            isMediumScreen -> 320.dp
            else -> 450.dp
        }

        WelcomeContentLayout(
            topPadding = topPadding,
            logoSize = logoSize,
            titleSize = titleSize,
            subtitleSize = subtitleSize,
            isSmallScreen = isSmallScreen,
            imageSpacing = imageSpacing,
            imageWidth = imageWidth,
            isTablet = isTablet,
            onGetStartedClick = onGetStartedClick,
            onTestConnectionClick = onTestConnectionClick
        )
    }
}

@Composable
private fun WelcomeContentLayout(
    topPadding: Dp,
    logoSize: TextUnit,
    titleSize: TextUnit,
    subtitleSize: TextUnit,
    isSmallScreen: Boolean,
    imageSpacing: Dp,
    imageWidth: Float,
    isTablet: Boolean,
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
            Spacer(modifier = Modifier.height(height = topPadding))

            AppLogoWithText(fontSize = logoSize)

            Spacer(modifier = Modifier.height(height = if (isSmallScreen) 12.dp else 16.dp))

            Text(
                text = stringResource(id = R.string.welcome_abroad),
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = titleSize,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(height = if (isSmallScreen) 4.dp else 8.dp))

            Text(
                text = stringResource(id = R.string.welcome_subtitle),
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Normal,
                fontSize = subtitleSize,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(height = if (isSmallScreen) 20.dp else 28.dp))

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

            Spacer(modifier = Modifier.height(imageSpacing))
        }

        Image(
            painter = painterResource(id = R.drawable.traveler),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(imageWidth),
            contentScale = ContentScale.FillWidth
        )
    }
}