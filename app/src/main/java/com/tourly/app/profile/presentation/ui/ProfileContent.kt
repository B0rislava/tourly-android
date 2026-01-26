package com.tourly.app.profile.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.domain.model.User
import com.tourly.app.home.domain.model.Tour
import com.tourly.app.login.domain.UserRole

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    user: User,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onEditTour: (Long) -> Unit = {},
    onDeleteTour: (Long) -> Unit = {},
    onCancelBooking: (Long) -> Unit = {},
    bookings: List<Booking> = emptyList(),
    tours: List<Tour> = emptyList()
) {
    if (user.role == UserRole.GUIDE) {
        GuideProfileContent(
            user = user,
            tours = tours,
            onLogout = onLogout,
            onEditProfile = onEditProfile,
            onEditTour = onEditTour,
            onDeleteTour = onDeleteTour,
            modifier = modifier
        )
    } else {
        TravelerProfileContent(
            user = user,
            bookings = bookings,
            onLogout = onLogout,
            onEditProfile = onEditProfile,
            onCancelBooking = onCancelBooking,
            modifier = modifier
        )
    }
}
