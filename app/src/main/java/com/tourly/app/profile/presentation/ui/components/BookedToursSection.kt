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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun BookedToursSection(
    bookings: List<Booking>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "My Bookings",
            style = MaterialTheme.typography.titleLarge,
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (bookings.isEmpty()) {
            Text(
                text = "You haven't booked any tours yet.",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = OutfitFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            bookings.forEach { booking ->
                BookingCard(booking = booking)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun BookingCard(booking: Booking) {
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
            Text(
                text = booking.tourTitle,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold
            )
            
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
                    text = "Date: ${booking.tourScheduledDate ?: "TBA"}",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = OutfitFamily
                )
                
                Text(
                    text = "${booking.numberOfParticipants} people",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = OutfitFamily
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = booking.status,
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = OutfitFamily,
                    color = if (booking.status == "CONFIRMED") 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
                
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
