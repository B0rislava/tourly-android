package com.tourly.app.profile.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.tourly.app.core.presentation.ui.components.UserAvatar
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.login.domain.UserRole
import androidx.compose.ui.res.stringResource
import com.tourly.app.R

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    firstName: String,
    lastName: String,
    email: String,
    role: UserRole,
    profilePictureUrl: String?,
    isOwnProfile: Boolean = true,
) {
    val hasIncompleteName = isOwnProfile && lastName.isBlank()
    var showTooltip by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        UserAvatar(
            imageUrl = profilePictureUrl,
            name = "$firstName $lastName",
            modifier = Modifier
                .size(120.dp)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = CircleShape
                ),
            textStyle = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        RoleBadge(role = role)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Name with warning badge if incomplete
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$firstName $lastName".trim(),
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = OutfitFamily
            )
            
            if (hasIncompleteName) {
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = stringResource(id = R.string.incomplete_profile_tooltip),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showTooltip = !showTooltip }
                    )
                    
                    // Tooltip using Popup - completely separate from layout
                    if (showTooltip) {
                        Popup(
                            alignment = Alignment.TopCenter,
                            offset = IntOffset(0, -140)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shadowElevation = 8.dp,
                                tonalElevation = 2.dp
                            ) {
                                Text(
                                    text = stringResource(id = R.string.incomplete_profile_tooltip),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = OutfitFamily,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = OutfitFamily
        )
    }
}


@Preview(name = "Complete Name", showBackground = true)
@Composable
private fun ProfileHeaderPreview() {
    ProfileHeader(
        firstName = "Ashley",
        lastName = "Watson",
        email = "ashley.watson@example.com",
        profilePictureUrl = null,
        role = UserRole.TRAVELER
    )
}

@Preview(name = "Incomplete Name (Missing Last Name)", showBackground = true)
@Composable
private fun ProfileHeaderIncompletePreview() {
    ProfileHeader(
        firstName = "Borislava",
        lastName = "",
        email = "borislava@gmail.com",
        profilePictureUrl = null,
        role = UserRole.TRAVELER
    )
}
