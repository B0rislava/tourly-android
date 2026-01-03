package com.tourly.app.core.presentation.ui.components.foundation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun AppLogoWithText(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 32.sp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AppLogo()

        Text(
            text = stringResource(id = R.string.app_name),
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}