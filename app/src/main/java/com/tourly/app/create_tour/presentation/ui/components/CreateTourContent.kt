package com.tourly.app.create_tour.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.create_tour.domain.TourType
import com.tourly.app.create_tour.presentation.state.CreateTourUiState

@Composable
fun CreateTourContent(
    modifier: Modifier = Modifier,
    state: CreateTourUiState,
    onTourTypeChanged: (TourType) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onDurationChanged: (String) -> Unit,
    onMaxGroupSizeChanged: (String) -> Unit,
    onPriceChanged: (Double) -> Unit,
    onWhatsIncludedChanged: (String) -> Unit,
    onAddDay: () -> Unit,
    onRemoveDay: (Int) -> Unit,
    onDayDescriptionChanged: (Int, String) -> Unit,
    onCreateTour: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SectionTitle(title = "Tour Type")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TourTypeCard(
                title = "Single Day",
                subtitle = "One day tour",
                selected = state.type == TourType.SINGLE_DAY,
                onClick = { onTourTypeChanged(TourType.SINGLE_DAY) },
                modifier = Modifier.weight(1f)
            )
            TourTypeCard(
                title = "Multi-Day",
                subtitle = "Multiple days",
                selected = state.type == TourType.MULTI_DAY,
                onClick = { onTourTypeChanged(TourType.MULTI_DAY) },
                modifier = Modifier.weight(1f)
            )
        }

        // Single Day: Images first
        if (state.type == TourType.SINGLE_DAY) {
            SectionTitle(title = "Tour Images")
            AddPhotoPlaceholder()
            Text(
                "Add up to 5 photos of your tour",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontFamily = OutfitFamily
            )
        }

        // Title & Description
        SectionTitle(title = "Tour Title")
        CustomTextField(
            value = state.title,
            onValueChange = onTitleChanged,
            placeholder = "e.g., Hidden Gems of Old Town"
        )

        SectionTitle(title = "Description")
        CustomTextField(
            value = state.description,
            onValueChange = onDescriptionChanged,
            placeholder = "Describe your tour experience, what travelers will see and do...",
            singleLine = false,
            minLines = 4
        )

        // TODO: Map location

        SectionTitle(title = "Location", icon = Icons.Outlined.LocationOn)
        CustomTextField(
            value = state.location,
            onValueChange = onLocationChanged,
            placeholder = "e.g., Barcelona, Spain"
        )

        // Day-by-Day Programme (Multi-Day)
        if (state.type == TourType.MULTI_DAY) {
            SectionTitle(title = "Day-by-Day Programme", icon = Icons.Outlined.CalendarToday)

            state.days?.forEachIndexed { index, day ->
                DayInputCard(
                    dayNumber = day.dayNumber,
                    description = day.description,
                    onDescriptionChange = { onDayDescriptionChanged(index, it) },
                    canRemove = (state.days.size) > 2,
                    onRemove = { onRemoveDay(index) }
                )
            }

            OutlinedButton(
                onClick = onAddDay,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Day", color = Color.Black, fontFamily = OutfitFamily)
            }

            // Images AFTER days for Multi-Day
            Spacer(modifier = Modifier.height(8.dp))
            SectionTitle(title = "Tour Images")
            AddPhotoPlaceholder()
        }

        // Duration / Group / Pricing
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SectionTitle(
                    title = if(state.type == TourType.MULTI_DAY) "Total Days" else "Duration",
                    icon = Icons.Outlined.AccessTime
                )
                CustomTextField(
                    value = state.duration,
                    onValueChange = onDurationChanged,
                    placeholder = if(state.type == TourType.MULTI_DAY) "2 days" else "e.g., 3 hours",
                    keyboardType = if (state.type == TourType.MULTI_DAY) KeyboardType.Number else KeyboardType.Text
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                SectionTitle(title = "Max Group", icon = Icons.Outlined.Group)
                CustomTextField(
                    value = state.maxGroupSize.toString(),
                    onValueChange = onMaxGroupSizeChanged,
                    placeholder = "e.g., 10",
                    keyboardType = KeyboardType.Number
                )
            }
        }

        SectionTitle(title = "Pricing", icon = Icons.Outlined.AttachMoney)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PricingRow(
                    label = "Price per person",
                    value = state.pricePerPerson.toString(),
                    onValueChange = { onPriceChanged(it.toDoubleOrNull() ?: 0.0) }
                )
            }
        }

        SectionTitle(title = "What's Included")
        CustomTextField(
            value = state.whatsIncluded,
            onValueChange = onWhatsIncludedChanged,
            placeholder = "e.g., Local snacks, transportation, entrance fees...",
            singleLine = false,
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCreateTour,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Create Tour Listing",
                fontSize = 18.sp,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}