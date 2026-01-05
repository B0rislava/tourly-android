package com.tourly.app.create_tour.presentation.ui

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
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tourly.app.core.presentation.ui.components.foundation.PrimaryButton
import com.tourly.app.create_tour.presentation.state.CreateTourUiState
import com.tourly.app.create_tour.presentation.ui.components.AddPhotoPlaceholder
import com.tourly.app.create_tour.presentation.ui.components.CustomTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.tourly.app.R
import com.tourly.app.create_tour.presentation.ui.components.DurationVisualTransformation
import com.tourly.app.create_tour.presentation.ui.components.PricingRow
import com.tourly.app.create_tour.presentation.ui.components.TourDatePickerDialog
import com.tourly.app.home.presentation.ui.components.SectionTitle
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun CreateTourContent(
    modifier: Modifier = Modifier,
    state: CreateTourUiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onDurationChanged: (String) -> Unit,
    onMaxGroupSizeChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onWhatsIncludedChanged: (String) -> Unit,
    onScheduledDateChanged: (Long?) -> Unit,
    onCreateTour: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SectionTitle(
            modifier = Modifier,
            title = stringResource(id = R.string.tour_images),
            icon = Icons.Outlined.Image
        )
        AddPhotoPlaceholder()

        SectionTitle(
            modifier = Modifier,
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
            modifier = Modifier,
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

        // TODO: Map location

        SectionTitle(
            modifier = Modifier,
            title = stringResource(id = R.string.tour_location),
            icon = Icons.Outlined.LocationOn
        )
        CustomTextField(
            value = state.location,
            onValueChange = onLocationChanged,
            placeholder = stringResource(id = R.string.tour_location_example),
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

        SectionTitle(
            modifier = Modifier,
            title = stringResource(id = R.string.tour_date),
            icon = Icons.Outlined.CalendarToday
        )
        Box {
            CustomTextField(
                value = state.scheduledDate?.let {
                    Instant.ofEpochMilli(it)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()
                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                } ?: "",
                onValueChange = {},
                placeholder = stringResource(id = R.string.tour_date_example),
                error = state.dateError
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }

        // Duration / Group / Pricing
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SectionTitle(
                    modifier = Modifier,
                    title = stringResource(id = R.string.tour_duration),
                    icon = Icons.Outlined.AccessTime
                )
                Spacer(Modifier.height(10.dp))

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
                    modifier = Modifier,
                    title = stringResource(id = R.string.tour_max_group),
                    icon = Icons.Outlined.Group
                )
                Spacer(Modifier.height(10.dp))

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
            modifier = Modifier,
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
            modifier = Modifier,
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

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = stringResource(id = R.string.create_tour),
            onClick = onCreateTour,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            isLoading = state.isLoading
        )
    }
}