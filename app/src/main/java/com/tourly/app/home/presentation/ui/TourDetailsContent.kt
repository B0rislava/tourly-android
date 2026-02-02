package com.tourly.app.home.presentation.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.tourly.app.R
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.core.domain.model.Review
import com.tourly.app.home.presentation.ui.components.GuideCard
import com.tourly.app.home.presentation.ui.components.InfoRow
import com.tourly.app.home.presentation.ui.components.ReviewItem

import com.tourly.app.login.domain.UserRole

@Composable
fun TourDetailsContent(
    tour: Tour,
    reviews: List<Review> = emptyList(),
    userRole: UserRole? = null,
    onBackClick: () -> Unit,
    onGuideClick: (Long) -> Unit = {},
    onEditTour: (Long) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Header Image Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AsyncImage(
                model = tour.imageUrl ?: R.drawable.tour_placeholder,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Top Bar Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (userRole == UserRole.GUIDE) {
                        IconButton(
                            onClick = { onEditTour(tour.id) },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(id = R.string.edit_tour),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    IconButton(
                        onClick = { /* Share */ },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = stringResource(id = R.string.share),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { /* Favorite */ },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.favorite),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-32).dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Title and Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = tour.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF0E3)
                        // TODO: Remove hardcoded colors
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${tour.rating}",
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " (${tour.reviewsCount})",
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Basic Info Items
                InfoRow(icon = Icons.Default.LocationOn, text = tour.location)
                Spacer(modifier = Modifier.height(8.dp))
                if (tour.scheduledDate.isNotEmpty()) {
                    InfoRow(icon = Icons.Default.CalendarToday, text = tour.scheduledDate) // Needs formatting
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (!tour.startTime.isNullOrEmpty()) {
                    InfoRow(
                        icon = Icons.Default.AccessTime,
                        text = "${stringResource(id = R.string.start_time)}: ${tour.startTime}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoRow(
                        icon = Icons.Default.AccessTime,
                        text = "${stringResource(id = R.string.duration_label)}: ${tour.duration}"
                    )
                    InfoRow(
                        icon = Icons.Default.Groups, 
                        text = stringResource(id = R.string.places_count, tour.availableSpots, tour.maxGroupSize),
                        textColor = if (tour.availableSpots > 0) Color.Gray else MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Guide Section
                Text(
                    text = stringResource(id = R.string.your_guide),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                GuideCard(tour, onGuideClick = onGuideClick)

                Spacer(modifier = Modifier.height(32.dp))

                // About Section
                Text(
                    text = stringResource(id = R.string.about_this_tour),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tour.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Meeting Location (Static Map)
                Text(
                    text = stringResource(id = R.string.meeting_location),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                val tourLocation = LatLng(tour.latitude ?: 42.6977, tour.longitude ?: 23.3219)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(tourLocation, 15f)
                }
                
                val markerState = rememberUpdatedMarkerState(position = tourLocation)

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        scrollGesturesEnabled = false,
                        zoomGesturesEnabled = false,
                        tiltGesturesEnabled = false,
                        rotationGesturesEnabled = false
                    )
                ) {
                    Marker(
                        state = markerState,
                        title = stringResource(id = R.string.meeting_point),
                        draggable = false
                    )
                }
                val context = LocalContext.current
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tour.meetingPoint ?: tour.location,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = {
                        val address = tour.meetingPoint ?: tour.location
                        val gmmIntentUri = "geo:0,0?q=${Uri.encode(address)}".toUri()
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        context.startActivity(mapIntent)
                    }) {
                        Text(stringResource(id = R.string.open_in_maps))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // What's included
                Text(
                    text = stringResource(id = R.string.tour_included),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Assuming bullet list in string
                val items = tour.whatsIncluded?.split(",") ?: listOf("Expert guide", "Safety equipment")
                items.forEach { item ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFFFF9800).copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp).background(Color(0xFFFFF0E3), CircleShape).padding(2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = item.trim(), style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Cancellation Policy
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.cancellation_policy),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Render policy points
                val policies = listOf(
                    stringResource(id = R.string.policy_full_refund),
                    stringResource(id = R.string.policy_partial_refund),
                    stringResource(id = R.string.policy_no_refund)
                )
                policies.forEach { policy ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = policy, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Reviews Preview
                Text(
                    text = stringResource(id = R.string.reviews_count, tour.reviewsCount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                if (reviews.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.no_reviews_yet),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                } else {
                    reviews.forEach { review ->
                        ReviewItem(review)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

