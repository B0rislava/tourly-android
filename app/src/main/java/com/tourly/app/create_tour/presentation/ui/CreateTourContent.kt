package com.tourly.app.create_tour.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom
import com.google.android.gms.maps.model.LatLng
import com.tourly.app.core.domain.model.LocationPrediction
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.components.foundation.PrimaryButton
import com.tourly.app.create_tour.presentation.state.CreateTourUiState
import com.tourly.app.create_tour.presentation.ui.components.TourImagePicker
import com.tourly.app.create_tour.presentation.ui.components.CustomTextField
import com.tourly.app.create_tour.presentation.ui.components.DurationVisualTransformation
import com.tourly.app.create_tour.presentation.ui.components.LocationSearchField
import com.tourly.app.create_tour.presentation.ui.components.PricingRow
import com.tourly.app.create_tour.presentation.ui.components.TagSelector
import com.tourly.app.create_tour.presentation.ui.components.TourDatePickerDialog
import com.tourly.app.home.presentation.ui.components.SectionTitle
import java.time.Instant.ofEpochMilli
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ofLocalizedDate
import java.time.format.DateTimeFormatter.ofLocalizedTime
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTourContent(
    modifier: Modifier = Modifier,
    state: CreateTourUiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onDurationChanged: (String) -> Unit,
    onMaxGroupSizeChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onWhatsIncludedChanged: (String) -> Unit,
    onScheduledDateChanged: (Long?) -> Unit,
    onStartTimeChanged: (LocalTime?) -> Unit,
    onTagToggled: (Long) -> Unit,
    onImageSelected: () -> Unit,
    onLocationPredictionClick: (LocationPrediction) -> Unit,
    addressPredictions: List<LocationPrediction>,
    onMeetingPointAddressChanged: (String) -> Unit,
    onMeetingPointSelected: (Double, Double) -> Unit,
    onCreateTour: () -> Unit,
    buttonText: String = stringResource(id = R.string.create_tour),
    onButtonClick: () -> Unit = onCreateTour
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SectionTitle(
            title = stringResource(id = R.string.tour_image),
            icon = Icons.Outlined.Image
        )
        TourImagePicker(
            selectedImageUri = state.imageUri,
            onClick = onImageSelected
        )

        SectionTitle(
            title = stringResource(id = R.string.tour_title),
            icon = Icons.Outlined.BookmarkBorder
        )
        CustomTextField(
            value = state.title,
            onValueChange = onTitleChanged,
            placeholder = stringResource(id = R.string.tour_title_example),
            error = state.titleError
        )

        SectionTitle(
            title = stringResource(id = R.string.tour_description),
            icon = Icons.Outlined.BookmarkBorder
        )
        CustomTextField(
            value = state.description,
            onValueChange = onDescriptionChanged,
            placeholder = stringResource(id = R.string.tour_description_example),
            singleLine = false,
            minLines = 4,
            error = state.descriptionError
        )

        val cameraPositionState = rememberCameraPositionState {
            position = fromLatLngZoom(
                LatLng(state.latitude ?: 42.6977, state.longitude ?: 23.3219),
                15f
            )
        }

        LaunchedEffect(state.latitude, state.longitude) {
            if (state.latitude != null && state.longitude != null) {
                cameraPositionState.position = fromLatLngZoom(
                    LatLng(state.latitude, state.longitude),
                    15f
                )
            }
        }

        @Suppress("DEPRECATION")
        val markerState = rememberMarkerState()

        LaunchedEffect(state.latitude, state.longitude) {
            if (state.latitude != null && state.longitude != null) {
                markerState.position = LatLng(state.latitude, state.longitude)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    onMeetingPointSelected(latLng.latitude, latLng.longitude)
                }
            ) {
                if (state.latitude != null && state.longitude != null) {
                    Marker(
                        state = markerState,
                        title = stringResource(id = R.string.meeting_point),
                        draggable = true
                    )
                }
            }
        }

        SectionTitle(
            title = stringResource(id = R.string.tour_location),
            icon = Icons.Outlined.LocationOn
        )
        LocationSearchField(
            value = state.meetingPointAddress,
            onValueChange = onMeetingPointAddressChanged,
            predictions = addressPredictions,
            onPredictionClick = onLocationPredictionClick,
            placeholder = stringResource(id = R.string.enter_meeting_address),
            error = state.locationError
        )

        var showDatePicker by remember { mutableStateOf(false) }

        TourDatePickerDialog(
            show = showDatePicker,
            selectedDateMillis = state.scheduledDate,
            onConfirm = {
                onScheduledDateChanged(it)
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
        
        var showTimePicker by remember { mutableStateOf(false) }

        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = state.startTime?.hour ?: 12,
                initialMinute = state.startTime?.minute ?: 0,
                is24Hour = true
            )
            
            var showingPicker by remember { mutableStateOf(true) }

            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onStartTimeChanged(LocalTime.of(timePickerState.hour, timePickerState.minute))
                            showTimePicker = false
                        }
                    ) {
                        Text(stringResource(id = R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                },

                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { showingPicker = !showingPicker }
                        ) {
                            Icon(
                                imageVector = if (showingPicker) Icons.Outlined.Keyboard else Icons.Outlined.AccessTime,
                                contentDescription = stringResource(id = R.string.toggle_input_mode)
                            )
                        }

                        if (showingPicker) {
                            TimePicker(state = timePickerState)
                        } else {
                            TimeInput(state = timePickerState)
                        }
                    }
                }
            )
        }

        SectionTitle(
            title = stringResource(id = R.string.tour_date),
            icon = Icons.Outlined.CalendarToday
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CustomTextField(
                    value = state.scheduledDate?.let {
                        ofEpochMilli(it)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                            .format(ofLocalizedDate(FormatStyle.MEDIUM))
                    } ?: "",
                    onValueChange = {},
                    placeholder = stringResource(id = R.string.tour_date_example),
                    error = state.dateError,
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }
            
            Box(modifier = Modifier.weight(1f)) {
                CustomTextField(
                    value = state.startTime?.format(ofLocalizedTime(FormatStyle.SHORT)) ?: "",
                    onValueChange = {},
                    placeholder = stringResource(id = R.string.time_example),
                    error = state.timeError,
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showTimePicker = true }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SectionTitle(
                    title = stringResource(id = R.string.tour_duration),
                    icon = Icons.Outlined.AccessTime
                )
                CustomTextField(
                    value = state.duration,
                    onValueChange = onDurationChanged,
                    placeholder = stringResource(id = R.string.tour_duration_example),
                    keyboardType = KeyboardType.Number,
                    visualTransformation = DurationVisualTransformation(),
                    error = state.durationError
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                SectionTitle(
                    title = stringResource(id = R.string.tour_max_group),
                    icon = Icons.Outlined.Group
                )
                CustomTextField(
                    value = state.maxGroupSize,
                    onValueChange = onMaxGroupSizeChanged,
                    placeholder = stringResource(id = R.string.tour_max_group_example),
                    keyboardType = KeyboardType.Number,
                    error = state.maxGroupSizeError
                )
            }
        }

        SectionTitle(
            title = stringResource(id = R.string.tour_price),
            icon = Icons.Outlined.AttachMoney
        )

        PricingRow(
            label = stringResource(id = R.string.tour_price_per_person),
            value = state.pricePerPerson,
            onValueChange = onPriceChanged,
            isError = state.priceError != null,
            errorMessage = state.priceError
        )

        SectionTitle(
            title = stringResource(id = R.string.tour_included),
            icon = Icons.Outlined.Add
        )
        CustomTextField(
            value = state.whatsIncluded,
            onValueChange = onWhatsIncludedChanged,
            placeholder = stringResource(id = R.string.tour_included_example),
            singleLine = false,
            minLines = 3
        )

        SectionTitle(
            title = stringResource(id = R.string.tour_tags),
            icon = Icons.AutoMirrored.Outlined.Label
        )
        
        TagSelector(
            availableTags = state.availableTags,
            selectedTagIds = state.selectedTagIds,
            onTagToggled = onTagToggled
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = buttonText,
            onClick = onButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            isLoading = state.isLoading
        )
    }
}
