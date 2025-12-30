package com.tourly.app.home.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.size
import com.tourly.app.R

@Composable
fun HomeContent(
    userId: String,
    email: String,
    firstName: String,
    lastName: String,
    profilePictureUrl: String?,
    onLogoutClick: () -> Unit
) {
    val defaultProfilePicture = "https://api.dicebear.com/9.x/avataaars/png?seed=$email"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = profilePictureUrl ?: defaultProfilePicture,
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.padding(16.dp))

        Text(
            text = "Welcome, $firstName $lastName!",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "User ID: $userId",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "email: $email",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.padding(16.dp))

        Button(
            onClick = onLogoutClick
        ) {
            Text(stringResource(R.string.logout))
        }
    }
}