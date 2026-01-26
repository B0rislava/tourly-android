package com.tourly.app.home.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import java.util.Locale
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun BookingDialog(
    tourId: Long,
    pricePerPerson: Double,
    maxParticipants: Int,
    isBooking: Boolean,
    onConfirm: (Long, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var participants by remember { mutableIntStateOf(1) }
    val totalPrice = pricePerPerson * participants

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Book Tour",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Number of Participants",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = OutfitFamily
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (participants > 1) participants-- },
                        enabled = !isBooking && participants > 1
                    ) {
                        Text("-")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = participants.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = OutfitFamily
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(
                        onClick = { if (participants < maxParticipants) participants++ },
                        enabled = !isBooking && participants < maxParticipants
                    ) {
                        Text("+")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Total Price: $${String.format(Locale.US, "%.2f", totalPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = OutfitFamily,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isBooking) {
                    CircularProgressIndicator()
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = { onConfirm(tourId, participants) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}
