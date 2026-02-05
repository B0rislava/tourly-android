package com.tourly.app.onboarding.presentation.ui

import com.tourly.app.core.presentation.ui.utils.WindowSizeState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.core.presentation.ui.components.foundation.AuthBackground
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import coil.decode.SvgDecoder

@Composable
fun WelcomeContent(
    windowSizeState: WindowSizeState,
    onGetStartedClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AuthBackground()
        
        val isCompactHeight = windowSizeState.isHeightCompact
        val isExpandedWidth = windowSizeState.isWidthExpanded

        WelcomeContentLayout(
            isCompactHeight = isCompactHeight,
            isExpandedWidth = isExpandedWidth,
            onGetStartedClick = onGetStartedClick,
        )
    }
}

@Composable
private fun WelcomeContentLayout(
    isCompactHeight: Boolean,
    isExpandedWidth: Boolean,
    onGetStartedClick: () -> Unit,
) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            Image(
                painter = painterResource(id = R.drawable.tourly_logo),
                contentDescription = null,
                modifier = Modifier.height(if (isCompactHeight) 40.dp else 60.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(if (isCompactHeight) 16.dp else 24.dp))

            Text(
                text = stringResource(id = R.string.welcome_abroad),
                style = MaterialTheme.typography.displaySmall,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = 44.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(id = R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(if (isCompactHeight) 32.dp else 48.dp))

            Button(
                onClick = onGetStartedClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Start Now",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(if (isCompactHeight) 24.dp else 32.dp))

            // Use journey.svg from assets as the main illustration
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/journey.svg")
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompactHeight) 240.dp else 340.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.weight(1f))
        }
}