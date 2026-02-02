package com.tourly.app.bookings.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.tourly.app.core.presentation.ui.components.StarRatingInput
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun RateExperienceDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int, String) -> Unit
) {
    var tourRating by remember { mutableIntStateOf(0) }
    var guideRating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rate your experience",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tour Rating
                Text(
                    text = "Tour Rating",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = OutfitFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                StarRatingInput(rating = tourRating, onRatingChanged = { tourRating = it })

                Spacer(modifier = Modifier.height(16.dp))

                // Guide Rating
                Text(
                    text = "Guide Rating",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = OutfitFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                StarRatingInput(rating = guideRating, onRatingChanged = { guideRating = it })

                Spacer(modifier = Modifier.height(16.dp))

                // Comment
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment (optional)", fontFamily = OutfitFamily) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                if (isError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please rate both the tour and the guide",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = OutfitFamily
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = "Cancel", fontFamily = OutfitFamily)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (tourRating > 0 && guideRating > 0) {
                                onConfirm(tourRating, guideRating, comment)
                            } else {
                                isError = true
                            }
                        }
                    ) {
                        Text(text = "Submit", fontFamily = OutfitFamily)
                    }
                }
            }
        }
    }
}


