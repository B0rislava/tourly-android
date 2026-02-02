package com.tourly.app.profile.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.core.presentation.util.StatusUtils

@Composable
fun BookedToursSection(
    bookings: List<Booking>,
    title: String,
    onCancelBooking: (Long) -> Unit = {},
    onRateClick: (Long) -> Unit = {},
    onCompleteBooking: (Long) -> Unit = {},
    showRateButton: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (bookings.isEmpty()) {
            Text(
                text = stringResource(id = R.string.no_tours_in_section),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = OutfitFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            bookings.forEach { booking ->
                BookingCard(
                    booking = booking,
                    onCancelClick = { onCancelBooking(booking.id) },
                    onRateClick = { onRateClick(booking.id) },
                    onCompleteClick = { onCompleteBooking(booking.id) },
                    showRateButton = showRateButton
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: Booking,
    onCancelClick: () -> Unit = {},
    onRateClick: () -> Unit = {},
    onCompleteClick: () -> Unit = {},
    showRateButton: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.tourTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    if (booking.status == "CONFIRMED") {
                        TextButton(onClick = onCompleteClick) {
                            Text(
                                text = "Dev: Complete",
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        
                        TextButton(onClick = onCancelClick) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = booking.tourLocation,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = OutfitFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.date_label, booking.tourScheduledDate ?: "TBA"),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = OutfitFamily
                )
                
                Text(
                    text = stringResource(id = R.string.people_count, booking.numberOfParticipants),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = OutfitFamily
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = StatusUtils.getTranslatedStatus(booking.status),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = OutfitFamily,
                    color = if (booking.status == "CONFIRMED") 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
                
                if (showRateButton && booking.status == "COMPLETED" && !booking.hasReview) {
                    TextButton(onClick = onRateClick) {
                        Text(
                            text = stringResource(id = R.string.rate_experience),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "$${booking.totalPrice.toInt()}",
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
