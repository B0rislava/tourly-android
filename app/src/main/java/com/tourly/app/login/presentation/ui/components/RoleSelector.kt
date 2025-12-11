package com.tourly.app.login.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardTravel
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class UserRole { Traveler, Guide }

@Composable
fun RoleSelector(
    modifier: Modifier = Modifier,
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit
) {
    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .padding(all = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {

        RoleButton(
            text = "Traveler",
            icon = Icons.Outlined.Person,
            isActive = selectedRole == UserRole.Traveler,
            onClick = { onRoleSelected(UserRole.Traveler) },
            modifier = Modifier.weight(weight = 1f)
        )

        RoleButton(
            text = "Guide",
            icon = Icons.Outlined.CardTravel,
            isActive = selectedRole == UserRole.Guide,
            onClick = { onRoleSelected(UserRole.Guide) },
            modifier = Modifier.weight(weight = 1f)
        )
    }
}


