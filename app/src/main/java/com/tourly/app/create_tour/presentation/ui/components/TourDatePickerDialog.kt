package com.tourly.app.create_tour.presentation.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@Composable
fun TourDatePickerDialog(
    show: Boolean,
    selectedDateMillis: Long?,
    onConfirm: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val today = LocalDate.now()
    val currentYear = today.year

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis,
        yearRange = currentYear..(currentYear + 6),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectedDate =
                    Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()

                return !selectedDate.isBefore(today)
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(datePickerState.selectedDateMillis)
            }) {
                Text(
                    text = stringResource(id = android.R.string.ok),
                    fontFamily = OutfitFamily
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = android.R.string.cancel),
                    fontFamily = OutfitFamily
                )
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
