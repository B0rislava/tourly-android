package com.tourly.app.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tourly.app.R
import com.tourly.app.core.domain.model.Review
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.dashboard.presentation.ui.components.GuideReviewCard
import com.tourly.app.login.domain.UserRole
import java.util.Locale.getDefault

@Composable
fun TravelerProfileContent(
    modifier: Modifier = Modifier,
    user: User,
    isOwnProfile: Boolean = true,
    isSavingAvatar: Boolean = false,
    reviews: List<Review> = emptyList()
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Avatar + Name + Stats
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.profilePictureUrl != null) {
                        AsyncImage(
                            model = user.profilePictureUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = user.firstName.take(1) + user.lastName.take(1),
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Name & Stats
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        Text(
                            text = "${user.followingCount}",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(id = R.string.following),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = OutfitFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Stars Rating Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Draw 5 stars
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (i in 1..5) {
                        val icon = when {
                            user.rating >= i -> Icons.Default.Star
                            user.rating >= i - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                            else -> Icons.Default.StarOutline
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color(0xFFFFB800), // Gold color
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Text(
                    text = String.format(getDefault(), "%.1f", user.rating),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "(${pluralStringResource(id = R.plurals.reviews_label, count = user.reviewsCount, user.reviewsCount)})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = OutfitFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bio
        item {
            if (!user.bio.isNullOrBlank()) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(
                        text = stringResource(id = R.string.about_me),
                        style = MaterialTheme.typography.titleMedium,
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Reviews Header
        item {
            Text(
                text = stringResource(id = R.string.reviews),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Reviews List
        if (reviews.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No reviews available.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = OutfitFamily
                    )
                }
            }
        } else {
            items(reviews) { review ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    GuideReviewCard(review = review)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TravelerProfileContentPreview() {
    TravelerProfileContent(
        user = User(
            id = 2,
            email = "traveler@example.com",
            firstName = "Ashley",
            lastName = "Watson",
            role = UserRole.TRAVELER,
            profilePictureUrl = null,
            bio = "I love traveling the world and exploring new cultures.",
            rating = 4.8,
            reviewsCount = 42,
            followingCount = 15
        ),
        reviews = emptyList()
    )
}
