package com.tourly.app.dashboard.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

internal enum class DashboardTab {
    UPCOMING, PAST, SAVED
}

@Composable
internal fun DashboardTabs(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    upcomingCount: Int,
    savedCount: Int
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardTabItem(
                title = stringResource(id = R.string.upcoming),
                count = upcomingCount,
                isSelected = selectedTab == DashboardTab.UPCOMING,
                onClick = { onTabSelected(DashboardTab.UPCOMING) },
                modifier = Modifier.weight(1f)
            )
            DashboardTabItem(
                title = stringResource(id = R.string.past),
                isSelected = selectedTab == DashboardTab.PAST,
                onClick = { onTabSelected(DashboardTab.PAST) },
                modifier = Modifier.weight(1f)
            )
            DashboardTabItem(
                title = stringResource(id = R.string.saved_short),
                count = savedCount,
                isSelected = selectedTab == DashboardTab.SAVED,
                onClick = { onTabSelected(DashboardTab.SAVED) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DashboardTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    count: Int? = null
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxHeight()
            .then(if (isSelected) Modifier.shadow(2.dp, RoundedCornerShape(12.dp)) else Modifier)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = OutfitFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (count != null && count > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                ) {
                    Text(
                        text = count.toString(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
