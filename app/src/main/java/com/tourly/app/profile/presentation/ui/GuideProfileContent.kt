package com.tourly.app.profile.presentation.ui

import java.util.Locale

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.tourly.app.core.presentation.ui.components.TourlyAlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.home.domain.model.Tour
import com.tourly.app.login.domain.UserRole
import com.tourly.app.profile.presentation.ui.components.ProfileHeader
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@Composable
fun GuideProfileContent(
    modifier: Modifier = Modifier,
    user: User,
    tours: List<Tour>,
    onSeeMore: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var showDeleteAccountConfirmation by remember { mutableStateOf(false) }

    if (showDeleteAccountConfirmation) {
        TourlyAlertDialog(
            onDismissRequest = { showDeleteAccountConfirmation = false },
            onConfirm = {
                onDeleteAccount()
            },
            title = stringResource(id = R.string.delete_account),
            text = stringResource(id = R.string.delete_account_confirmation),
            confirmButtonText = "Delete",
            isDestructive = true
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            role = user.role,
            profilePictureUrl = user.profilePictureUrl
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GuideStatItem(
                icon = Icons.Default.Star,
                value = String.format(Locale.getDefault(), "%.1f", user.rating),
                label = "${user.reviewsCount} reviews",
                modifier = Modifier.weight(1f)
            )
            GuideStatItem(
                icon = Icons.Default.People,
                value = "${user.followerCount}",
                label = "Followers",
                modifier = Modifier.weight(1f)
            )
            GuideStatItem(
                icon = Icons.Default.WorkspacePremium,
                value = "${tours.size}",
                label = "Tours",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Primary Actions

        Button(
            onClick = { showDeleteAccountConfirmation = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.delete_account),
                fontFamily = OutfitFamily
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

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

        // Certifications Section
        if (!user.certifications.isNullOrBlank()) {
             Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Certifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.certifications,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = OutfitFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Tours Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Tours",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )
                
                if (tours.isNotEmpty()) {
                    TextButton(onClick = onSeeMore) {
                        Text(text = "See more", fontFamily = OutfitFamily)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (tours.isEmpty()) {
                Text(
                    text = "You haven't created any tours yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = OutfitFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(tours) { tour ->
                        CompactTourCard(tour = tour)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CompactTourCard(
    tour: Tour,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(240.dp),
        shape = RoundedCornerShape(12.dp),
        colors = cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tour.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = OutfitFamily,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tour.location,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = OutfitFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${tour.rating} ★",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = OutfitFamily
                )
                Text(
                    text = "$${tour.pricePerPerson.toInt()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = OutfitFamily
                )
            }
        }
    }
}

@Composable
fun GuideStatItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = OutfitFamily
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = OutfitFamily,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GuideProfileContentPreview() {
    GuideProfileContent(
        user = User(
            id = 1,
            email = "guide@example.com",
            firstName = "Ashley",
            lastName = "Watson",
            role = UserRole.GUIDE,
            profilePictureUrl = null,
            bio = "I am a passionate tour guide with over 10 years of experience in leading cultural and historical tours. I love sharing the hidden gems of my city with travelers from all over the world.",
            rating = 4.9,
            reviewsCount = 156,
            followerCount = 890,
            toursCompleted = 245,
            certifications = "Certified Cultural Guide, First Aid Certified"
        ),
        tours = listOf(
            Tour(
                id = 1,
                tourGuideId = 1,
                guideName = "Ashley Watson",
                title = "Historical Center Walking Tour",
                description = "Discover the rich history of our city.",
                location = "Sofia, Bulgaria",
                duration = "3 hours",
                maxGroupSize = 15,
                availableSpots = 10,
                pricePerPerson = 25.0,
                scheduledDate = "2024-05-20",
                createdAt = "2024-01-01",
                status = "ACTIVE",
                rating = 4.8,
                reviewsCount = 45,
                meetingPoint = "Serdika Metro Station",
                imageUrl = null,
                cancellationPolicy = "Free cancellation up to 24 hours before.",
                whatsIncluded = "Water, Snacks",
                guideBio = null,
                guideRating = 4.9,
                guideToursCompleted = 245,
                guideImageUrl = null
            )
        ),
        onSeeMore = {},
        onDeleteAccount = {}
    )
}
