package com.tourly.app.profile.presentation.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.login.domain.UserRole
import com.tourly.app.profile.presentation.ui.components.ProfileHeader
import com.tourly.app.profile.presentation.ui.components.ProfileStatItem
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
            ProfileStatItem(
                icon = Icons.Default.Star,
                value = String.format(getDefault(), "%.1f", user.rating),
                label = pluralStringResource(
                    id = R.plurals.reviews_label,
                    count = user.reviewsCount,
                    user.reviewsCount
                ),
                modifier = Modifier.weight(1f)
            )
            ProfileStatItem(
                icon = Icons.Default.People,
                value = "${user.followerCount}",
                label = stringResource(id = R.string.followers),
                modifier = Modifier.weight(1f)
            )
            ProfileStatItem(
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

        
        
        Spacer(modifier = Modifier.height(32.dp))
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
        )
    )
}
