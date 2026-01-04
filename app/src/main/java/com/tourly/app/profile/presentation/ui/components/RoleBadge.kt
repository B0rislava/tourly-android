package com.tourly.app.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardTravel
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.login.domain.UserRole
import java.util.Locale

@Composable
fun RoleBadge(
    modifier: Modifier = Modifier,
    role: UserRole,
) {
    val containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    val contentColor = MaterialTheme.colorScheme.primary
    val icon = when (role) {
        UserRole.GUIDE -> Icons.Outlined.Explore
        UserRole.TRAVELER -> Icons.Outlined.CardTravel
    }
    
    val roleText = role.name.lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = roleText,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.Medium
        )
    }
}
