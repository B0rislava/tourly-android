package com.tourly.app.profile.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.presentation.viewmodel.UserViewModel
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.login.domain.UserRole
import com.tourly.app.profile.presentation.ui.components.ProfileSkeleton

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userId: Long? = null,
    userViewModel: UserViewModel = hiltViewModel(),
    onTourClick: (Long) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val userState by userViewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        if (userId != null && userId != -1L) {
            userViewModel.fetchOtherUserProfile(userId)
        } else {
            userViewModel.refreshBookings(forceOwnProfile = true)
        }
    }

    val isOtherProfile = userId != null && userId != -1L

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Header without app-bar background (matches "My tours" / "Your group chats").
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = if (isOtherProfile) 4.dp else 16.dp,
                        end = if (isOtherProfile) 16.dp else 4.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isOtherProfile) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = com.tourly.app.R.string.back)
                        )
                    }
                }

                Text(
                    text = stringResource(id = com.tourly.app.R.string.profile),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = if (isOtherProfile) 20.dp else 0.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                if (!isOtherProfile) {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(id = com.tourly.app.R.string.settings)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (val state = userState) {
                    is UserUiState.Idle -> {
                        Text(
                            text = "No user data available",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = OutfitFamily
                        )
                    }
                    is UserUiState.Loading -> {
                        ProfileSkeleton()
                    }
                    is UserUiState.Success -> {
                        val isStale = (isOtherProfile == state.isOwnProfile) || 
                                      (isOtherProfile && userId != state.user.id)
                        
                        if (isStale) {
                            ProfileSkeleton()
                        } else {
                            ProfileContent(
                                user = state.user,
                                isOwnProfile = state.isOwnProfile,
                                onBackClick = onBackClick,
                                isSavingAvatar = state.isSavingAvatar,
                                tours = state.tours,
                                reviews = state.reviews,
                                onFollowClick = { userViewModel.toggleFollow() },
                                onTourClick = onTourClick
                            )
                        }
                    }
                    is UserUiState.Error -> {
                        Text(
                            text = "Error: ${state.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun GuideProfileContentPreview() {
    ProfileContent(
        user = User(
            id = 1,
            email = "guide@example.com",
            firstName = "John",
            lastName = "Guide",
            role = UserRole.GUIDE,
            profilePictureUrl = null,
            bio = "Experienced tour guide in Sofia.",
            rating = 4.8,
            reviewsCount = 120,
            followerCount = 450,
            toursCompleted = 85
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun TravelerProfileContentPreview() {
    ProfileContent(
        user = User(
            id = 2,
            email = "traveler@example.com",
            firstName = "Jane",
            lastName = "Traveler",
            role = UserRole.TRAVELER,
            profilePictureUrl = null
        )
    )
}