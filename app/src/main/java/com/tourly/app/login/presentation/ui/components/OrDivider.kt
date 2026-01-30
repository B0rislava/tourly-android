package com.tourly.app.login.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun OrDivider(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
        )
        Text(
            text = stringResource(id = R.string.or_with),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = OutfitFamily,
                fontSize = 14.sp
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
        )
    }
}
