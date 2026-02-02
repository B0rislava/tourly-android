package com.tourly.app.profile.presentation.ui


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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.login.domain.UserRole
import com.tourly.app.profile.presentation.ui.components.ProfileHeader
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import com.tourly.app.R
import java.util.Locale.getDefault

@Composable
fun GuideProfileContent(
    modifier: Modifier = Modifier,
    user: User,
    isOwnProfile: Boolean = true,
    tours: List<Tour>,
    onSeeMore: () -> Unit,
    onFollowClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isOwnProfile) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }

            }
        }
        
        ProfileHeader(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            role = user.role,
            profilePictureUrl = user.profilePictureUrl,
            isOwnProfile = isOwnProfile
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GuideStatItem(
                icon = Icons.Default.Star,
                value = String.format(getDefault(), "%.1f", user.rating),
                label = pluralStringResource(
                    id = R.plurals.reviews_label,
                    count = user.reviewsCount,
                    user.reviewsCount
                ),
                modifier = Modifier.weight(1f)
            )
            GuideStatItem(
                icon = Icons.Default.People,
                value = "${user.followerCount}",
                label = stringResource(id = R.string.followers),
                modifier = Modifier.weight(1f)
            )
            GuideStatItem(
                icon = Icons.Default.WorkspacePremium,
                value = "${tours.size}",
                label = stringResource(id = R.string.available_tours),
                modifier = Modifier.weight(1f)
            )

        }

        Spacer(modifier = Modifier.height(24.dp))

        // Primary Actions
        if (!isOwnProfile) {
            Button(
                onClick = onFollowClick,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (user.isFollowing) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
                    contentColor = if (user.isFollowing) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (user.isFollowing) stringResource(id = R.string.following) else stringResource(id = R.string.follow),
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = OutfitFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bio Section
        if (!user.bio.isNullOrBlank()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.about_me),
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
                    text = stringResource(id = R.string.certifications),
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
                    text = if (isOwnProfile) stringResource(id = R.string.my_tours) else stringResource(id = R.string.available_tours),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )
                
                if (tours.isNotEmpty()) {
                    TextButton(onClick = onSeeMore) {
                        Text(text = stringResource(id = R.string.see_more), fontFamily = OutfitFamily)
                    }
                }

            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (tours.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_tours_created),
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
                    text = String.format(getDefault(), "%.1f ★", tour.rating),
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
                whatsIncluded = "Water, Snacks",
                guideBio = null,
                guideRating = 4.9,
                guideImageUrl = null,
                startTime = "10:00"
            )
        ),
        onSeeMore = {}
    )
}
