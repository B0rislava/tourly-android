package com.tourly.app.profile.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.login.domain.UserRole

@Composable
fun ProfileHeader(
    firstName: String,
    lastName: String,
    email: String,
    role: UserRole,
    profilePictureUrl: String?,
    modifier: Modifier = Modifier
) {
    val defaultPainter = painterResource(id = R.drawable.ic_default_avatar)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = profilePictureUrl,
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            placeholder = defaultPainter,
            error = defaultPainter,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        RoleBadge(role = role)
        
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$firstName $lastName",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = OutfitFamily
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = OutfitFamily
        )
    }
}

@Preview(showBackground = true)
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
