package com.tourly.app.profile.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.login.domain.UserRole

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    user: User,
    isOwnProfile: Boolean = true,
    onBackClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    isSavingAvatar: Boolean = false,
    tours: List<Tour> = emptyList()
) {
    if (user.role == UserRole.GUIDE) {
        GuideProfileContent(
            user = user,
            isOwnProfile = isOwnProfile,
            isSavingAvatar = isSavingAvatar,
            tours = tours,
            onBackClick = onBackClick,
            onFollowClick = onFollowClick,
            modifier = modifier
        )
    } else {
        TravelerProfileContent(
            user = user,
            isOwnProfile = isOwnProfile,
            isSavingAvatar = isSavingAvatar,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
