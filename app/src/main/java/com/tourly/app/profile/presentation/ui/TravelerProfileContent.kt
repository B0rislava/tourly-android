package com.tourly.app.profile.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.login.domain.UserRole
import com.tourly.app.profile.presentation.ui.components.ProfileHeader

@Composable
fun TravelerProfileContent(
    modifier: Modifier = Modifier,
    user: User,
    isOwnProfile: Boolean = true,
    onBackClick: () -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isOwnProfile) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Start
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }

        ProfileHeader(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            role = user.role,
            profilePictureUrl = user.profilePictureUrl,
            isOwnProfile = isOwnProfile
        )



        // Bio Section
        if (!user.bio.isNullOrBlank()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "About me",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = OutfitFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TravelerProfileContentPreview() {
    TravelerProfileContent(
        user = User(
            id = 2,
            email = "traveler@example.com",
            firstName = "Ashley",
            lastName = "Watson",
            role = UserRole.TRAVELER,
            profilePictureUrl = null
        )
    )
}
