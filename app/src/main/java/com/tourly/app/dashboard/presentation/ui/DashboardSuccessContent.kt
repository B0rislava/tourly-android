package com.tourly.app.dashboard.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import com.tourly.app.bookings.presentation.ui.RateExperienceDialog
import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.core.presentation.ui.components.TourlyAlertDialog
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.core.presentation.util.Formatters
import com.tourly.app.dashboard.presentation.state.DashboardUiState
import com.tourly.app.dashboard.presentation.ui.components.CompactDashboardCard
import com.tourly.app.dashboard.presentation.ui.components.CompactGuideTourCard
import com.tourly.app.dashboard.presentation.ui.components.DashboardIcon
import com.tourly.app.dashboard.presentation.ui.components.EmptyState
import com.tourly.app.dashboard.presentation.ui.components.GuideBookingCard
import com.tourly.app.dashboard.presentation.ui.components.GuideDashboardTab
import com.tourly.app.dashboard.presentation.ui.components.GuideDashboardTabs
import com.tourly.app.dashboard.presentation.ui.components.GuideReviewCard
import com.tourly.app.dashboard.presentation.ui.components.StatCard
import com.tourly.app.login.domain.UserRole
import java.util.Locale

@Composable
internal fun DashboardSuccessContent(
    uiState: DashboardUiState.Success,
    onCancelBooking: (Long) -> Unit,
    onDeleteTour: (Long) -> Unit,
    onEditTour: (Long) -> Unit,
    onTourClick: (Long) -> Unit,
    onCreateTour: () -> Unit,
    onRateTour: (Long, Int, Int, String) -> Unit,
    onCompleteBooking: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var tourToDelete by remember { mutableStateOf<Tour?>(null) }
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }
    var showRateDialog by remember { mutableStateOf<Booking?>(null) }

    val onCancelBookingAction = { bookingId: Long ->
        bookingToCancel = uiState.bookings.find { it.id == bookingId }
    }
    val onRateTourAction = { bookingId: Long ->
        showRateDialog = uiState.bookings.find { it.id == bookingId }
    }

    if (tourToDelete != null) {
        TourlyAlertDialog(
            onDismissRequest = { tourToDelete = null },
            onConfirm = {
                tourToDelete?.id?.let { onDeleteTour(it) }
                tourToDelete = null
            },
            title = stringResource(id = R.string.delete_tour),
            text = stringResource(id = R.string.delete_tour_confirmation, tourToDelete?.title ?: ""),
            confirmButtonText = stringResource(id = R.string.delete),
            isDestructive = true
        )
    }

    if (bookingToCancel != null) {
        TourlyAlertDialog(
            onDismissRequest = { bookingToCancel = null },
            onConfirm = {
                bookingToCancel?.id?.let { onCancelBooking(it) }
                bookingToCancel = null
            },
            title = stringResource(id = R.string.cancel_booking),
            text = stringResource(id = R.string.cancel_booking_confirmation, bookingToCancel?.tourTitle ?: ""),
            confirmButtonText = stringResource(id = R.string.yes_cancel),
            dismissButtonText = stringResource(id = R.string.no_keep),
            isDestructive = true
        )
    }

    if (showRateDialog != null) {
        RateExperienceDialog(
            onDismissRequest = { showRateDialog = null },
            onConfirm = { tourRating, guideRating, comment ->
                showRateDialog?.id?.let { bookingId ->
                    onRateTour(bookingId, tourRating, guideRating, comment)
                }
                showRateDialog = null
            }
        )
    }

    var selectedTab by remember { mutableStateOf(DashboardTab.UPCOMING) }
    var selectedGuideTab by remember { mutableStateOf(GuideDashboardTab.TOURS) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (uiState.role == UserRole.TRAVELER) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp, bottom = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.my_tours),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(id = R.string.manage_bookings_saved),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = OutfitFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tabs
                DashboardTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    upcomingCount = uiState.bookings.count { it.status != "COMPLETED" && it.status != "CANCELLED" },
                    savedCount = uiState.savedTours.size
                )
            }
        }

        if (uiState.role == UserRole.TRAVELER) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val currentItems = when (selectedTab) {
                    DashboardTab.UPCOMING -> {
                        uiState.bookings
                            .filter { it.status != "COMPLETED" && it.status != "CANCELLED" }
                            .sortedBy { it.tourScheduledDate }
                    }
                    DashboardTab.PAST -> {
                        uiState.bookings
                            .filter { it.status == "COMPLETED" }
                            .sortedByDescending { it.tourScheduledDate }
                    }
                    DashboardTab.SAVED -> emptyList() // Handled separately due to different model
                }

                if (selectedTab == DashboardTab.SAVED) {
                    if (uiState.savedTours.isEmpty()) {
                        item { EmptyState(stringResource(id = R.string.no_tours_in_section)) }
                    } else {
                        items(uiState.savedTours) { tour ->
                            CompactDashboardCard(
                                title = tour.title,
                                thumbnailUrl = tour.imageUrl,
                                location = tour.location,
                                date = Formatters.formatDate(tour.scheduledDate),
                                isSaved = true,
                                onClick = { onTourClick(tour.id) }
                            )
                        }
                    }
                } else {
                    if (currentItems.isEmpty()) {
                        item { EmptyState(stringResource(id = R.string.no_tours_in_section)) }
                    } else {
                        items(currentItems) { booking ->
                            CompactDashboardCard(
                                title = booking.tourTitle,
                                thumbnailUrl = booking.tourImageUrl,
                                location = booking.tourLocation,
                                date = Formatters.formatDate(booking.tourScheduledDate),
                                status = booking.status,
                                onClick = { onTourClick(booking.tourId) },
                                 action1Text = when (selectedTab) {
                                     DashboardTab.PAST if !booking.hasReview -> {
                                         stringResource(id = R.string.rate_experience)
                                     }
                                     DashboardTab.UPCOMING if booking.status == "CONFIRMED" -> {
                                         "Dev: Complete"
                                     }
                                     else -> null
                                 },
                                onAction1Click = when (selectedTab) {
                                    DashboardTab.PAST if !booking.hasReview -> {
                                        { onRateTourAction(booking.id) }
                                    }
                                    DashboardTab.UPCOMING if booking.status == "CONFIRMED" -> {
                                        { onCompleteBooking(booking.id) }
                                    }
                                    else -> null
                                },
                                action2Text = if (selectedTab == DashboardTab.UPCOMING && booking.status == "CONFIRMED") {
                                    stringResource(id = R.string.cancel)
                                } else null,
                                onAction2Click = if (selectedTab == DashboardTab.UPCOMING && booking.status == "CONFIRMED") {
                                    { onCancelBookingAction(booking.id) }
                                } else null
                            )
                        }
                    }
                }
            }
        } else {
            // Guide Dashboard
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header
                item {
                    Column {
                        Text(
                            text = stringResource(id = R.string.guide_dashboard),
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(id = R.string.welcome_back_user, uiState.userName),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = OutfitFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Stats
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = stringResource(id = R.string.bookings),
                            value = uiState.bookings.size.toString(),
                            subtitle = stringResource(id = R.string.this_year),
                            icon = {
                                DashboardIcon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    size = 20.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = stringResource(id = R.string.rating),
                            value = String.format(Locale.getDefault(), "%.1f", uiState.guideRating),
                            subtitle = stringResource(id = R.string.reviews_plural, uiState.reviewsCount),
                            icon = {
                                DashboardIcon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    size = 20.dp,
                                    color = Color(0xFFFFB800)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Tabs
                item {
                    GuideDashboardTabs(
                        selectedTab = selectedGuideTab,
                        onTabSelected = { selectedGuideTab = it }
                    )
                }

                // Tab Content
                when (selectedGuideTab) {
                    GuideDashboardTab.TOURS -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.my_tours),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = OutfitFamily,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = onCreateTour) {
                                    DashboardIcon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        size = 18.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = stringResource(id = R.string.create_tour), fontFamily = OutfitFamily)
                                }
                            }
                        }
                        
                        if (uiState.tours.isEmpty()) {
                            item {
                                EmptyState(
                                    message = stringResource(id = R.string.no_tours_created)
                                )
                            }
                        } else {
                            items(uiState.tours) { tour ->
                                val tourBookings = uiState.bookings.count { it.tourId == tour.id }
                                CompactGuideTourCard(
                                    tour = tour,
                                    totalBookings = tourBookings,
                                    onEdit = { onEditTour(tour.id) },
                                    onDelete = { tourToDelete = tour }
                                )
                            }
                        }
                    }
                    GuideDashboardTab.BOOKINGS -> {
                        if (uiState.bookings.isEmpty()) {
                            item {
                                EmptyState(
                                    message = stringResource(id = R.string.no_bookings_found)
                                )
                            }
                        } else {
                            items(uiState.bookings) { booking ->
                                GuideBookingCard(booking = booking)
                            }
                        }
                    }
                    GuideDashboardTab.REVIEWS -> {
                        if (uiState.reviews.isEmpty()) {
                            item {
                                EmptyState(
                                    message = stringResource(id = R.string.no_reviews_dashboard)
                                )
                            }
                        } else {
                            items(uiState.reviews) { review ->
                                GuideReviewCard(review = review)
                            }
                        }
                    }
                }
            }
        }
    }
}
