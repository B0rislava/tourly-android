package com.tourly.app.profile.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.presentation.viewmodel.UserViewModel
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.login.domain.UserRole

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = hiltViewModel(),
    onSeeMore: () -> Unit = {}
) {
    val userState by userViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.refreshBookings()
    }

    Box(
        modifier = modifier.fillMaxSize(),
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
                CircularProgressIndicator()
            }
            is UserUiState.Success -> {
                ProfileContent(
                    user = state.user,
                    onSeeMore = onSeeMore,
                    tours = state.tours
                )
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