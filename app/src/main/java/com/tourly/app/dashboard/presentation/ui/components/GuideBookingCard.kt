package com.tourly.app.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tourly.app.R
import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.core.presentation.util.StatusUtils

@Composable
fun GuideBookingCard(
    booking: Booking,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (booking.customerImageUrl != null) {
                         AsyncImage(
                            model = booking.customerImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        val initials = booking.customerName?.split(" ")
                            ?.mapNotNull { it.firstOrNull() }
                            ?.take(2)
                            ?.joinToString("")
                            ?.uppercase() ?: "?"
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = booking.customerName ?: stringResource(id = R.string.unknown_user),
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = booking.tourTitle,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = OutfitFamily,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when(booking.status) {
                            "CONFIRMED" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            "COMPLETED" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                            else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        }
                    ) {
                        Text(
                            text = StatusUtils.getTranslatedStatus(booking.status),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = OutfitFamily,
                            color = when(booking.status) {
                                "CONFIRMED" -> MaterialTheme.colorScheme.primary
                                "COMPLETED" -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.error
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            DashboardIcon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                size = 14.dp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(text = booking.tourScheduledDate ?: "", style = MaterialTheme.typography.labelMedium, fontFamily = OutfitFamily, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            DashboardIcon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                size = 14.dp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(text = stringResource(id = R.string.guests_plural, booking.numberOfParticipants), style = MaterialTheme.typography.labelMedium, fontFamily = OutfitFamily, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    
                    Text(
                        text = "$${booking.totalPrice.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
