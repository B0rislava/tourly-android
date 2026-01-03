package com.tourly.app.profile.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import com.tourly.app.core.ui.theme.OutfitFamily
import com.tourly.app.profile.presentation.ui.components.ProfileHeader

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    firstName: String,
    lastName: String,
    email: String,
    profilePictureUrl: String?,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(
            firstName = firstName,
            lastName = lastName,
            email = email,
            profilePictureUrl = profilePictureUrl
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEditProfile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.edit_profile),
                fontFamily = OutfitFamily
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(id = R.string.logout),
                fontFamily = OutfitFamily
            )
        }

        // TODO: Add more profile sections here (About, Contacts, Certifications, Reviews, Tours, etc.)
    }
}

