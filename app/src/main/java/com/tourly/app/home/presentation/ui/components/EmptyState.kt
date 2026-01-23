package com.tourly.app.home.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title ?: stringResource(id = R.string.empty_heading),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description ?: stringResource(id = R.string.empty_subheading),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = OutfitFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
