package com.tourly.app.login.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardTravel
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.tourly.app.login.domain.UserRole

@Composable
fun RoleSelector(
    modifier: Modifier = Modifier,
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit
) {
    val indicatorProgress by animateFloatAsState(
        targetValue = if (selectedRole == UserRole.TRAVELER) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "indicator_offset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp)
    ) {
        // INDICATOR
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .graphicsLayer {
                    translationX = size.width * indicatorProgress
                }
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(12.dp)
                )
        )

        Row(modifier = Modifier.fillMaxSize()) {
            RoleButton(
                text = "Traveler",
                icon = Icons.Outlined.CardTravel,
                isActive = selectedRole == UserRole.TRAVELER,
                onClick = { onRoleSelected(UserRole.TRAVELER) },
                modifier = Modifier.weight(1f)
            )

            RoleButton(
                text = "Guide",
                icon = Icons.Outlined.Explore,
                isActive = selectedRole == UserRole.GUIDE,
                onClick = { onRoleSelected(UserRole.GUIDE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
