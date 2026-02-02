package com.tourly.app.home.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import androidx.compose.ui.window.Dialog
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.core.domain.model.TourFilters
import com.tourly.app.core.domain.model.LocationPrediction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun FilterButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: Any? = null,
    onClick: () -> Unit,
    isActive: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                if (icon is ImageVector) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                } else if (icon is Int) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = OutfitFamily
            )
            if (!isActive) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp).padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun DateFilterButton(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Surface(
        onClick = { showDatePicker = true },
        shape = CircleShape,
        color = if (selectedDate != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.size(56.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
             Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Select Date",
                tint = if (selectedDate != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showDatePicker) {
        TourDatePickerDialog(
            initialDate = selectedDate,
            onDateSelected = { 
                onDateSelected(it)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourDatePickerDialog(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.atStartOfDay(ZoneId.of("UTC"))?.toInstant()?.toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                    onDateSelected(date)
                } ?: onDateSelected(null)
            }) {
                Text(stringResource(id = R.string.apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun SortMenu(
    currentSortField: TourFilters.SortField,
    currentSortOrder: TourFilters.SortOrder,
    onSortSelected: (TourFilters.SortField, TourFilters.SortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        FilterButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.sort_by),
            icon = Icons.AutoMirrored.Filled.Sort,
            onClick = { expanded = true },
            isActive = false // Always neutral style
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
             modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            SortOptionItem(
                label = stringResource(id = R.string.top_rated),
                isSelected = currentSortField == TourFilters.SortField.RATING,
                onClick = { 
                    onSortSelected(TourFilters.SortField.RATING, TourFilters.SortOrder.DESC)
                    expanded = false
                }
            )
            SortOptionItem(
                label = stringResource(id = R.string.price_asc),
                isSelected = currentSortField == TourFilters.SortField.PRICE && currentSortOrder == TourFilters.SortOrder.ASC,
                onClick = { 
                    onSortSelected(TourFilters.SortField.PRICE, TourFilters.SortOrder.ASC)
                    expanded = false
                }
            )
            SortOptionItem(
                label = stringResource(id = R.string.price_desc),
                isSelected = currentSortField == TourFilters.SortField.PRICE && currentSortOrder == TourFilters.SortOrder.DESC,
                onClick = { 
                    onSortSelected(TourFilters.SortField.PRICE, TourFilters.SortOrder.DESC)
                    expanded = false
                }
            )
             SortOptionItem(
                label = stringResource(id = R.string.duration_asc),
                isSelected = currentSortField == TourFilters.SortField.DURATION && currentSortOrder == TourFilters.SortOrder.ASC,
                onClick = { 
                    onSortSelected(TourFilters.SortField.DURATION, TourFilters.SortOrder.ASC)
                    expanded = false
                }
            )
            SortOptionItem(
                label = stringResource(id = R.string.duration_desc),
                isSelected = currentSortField == TourFilters.SortField.DURATION && currentSortOrder == TourFilters.SortOrder.DESC,
                onClick = { 
                    onSortSelected(TourFilters.SortField.DURATION, TourFilters.SortOrder.DESC)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun SortOptionItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { 
            Text(
                text = label, 
                fontFamily = OutfitFamily,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            ) 
        },
        onClick = onClick,
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else null
    )
}

@Composable
fun PriceFilterPopup(
    priceRange: ClosedFloatingPointRange<Float>,
    onPriceRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var tempRange by remember(priceRange) { mutableStateOf(priceRange) }

    val isActive = priceRange.start > 0f || priceRange.endInclusive < 500f
    
    Box(modifier = modifier) {
        FilterButton(
            modifier = Modifier.fillMaxWidth(),
            text = if (isActive) "$${priceRange.start.toInt()} - $${priceRange.endInclusive.toInt()}" else stringResource(id = R.string.tour_price),
            icon = Icons.Default.AttachMoney, 
             onClick = { expanded = true },
            isActive = isActive
        )

        if (expanded) {
            Dialog(onDismissRequest = { expanded = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.price_range),
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = OutfitFamily
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "$${tempRange.start.toInt()} - $${tempRange.endInclusive.toInt()}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = OutfitFamily,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.End)
                        )
                        
                        RangeSlider(
                            value = tempRange,
                            onValueChange = { tempRange = it },
                            valueRange = 0f..500f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "$0", style = MaterialTheme.typography.labelMedium)
                            Text(text = "$500+", style = MaterialTheme.typography.labelMedium)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                onPriceRangeChanged(tempRange)
                                expanded = false 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(id = R.string.apply))
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun LocationFilterPopup(
    selectedLocation: String?,
    predictions: List<LocationPrediction>,
    onSearchTextChange: (String) -> Unit,
    onLocationSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(selectedLocation ?: "") }
    

    Box(modifier = modifier) {
        FilterButton(
            modifier = Modifier.fillMaxWidth(),
            text = selectedLocation ?: stringResource(id = R.string.tour_location),
            icon = Icons.Default.LocationOn,
            onClick = { expanded = true },
            isActive = selectedLocation != null
        )

        if (expanded) {
            Dialog(onDismissRequest = { expanded = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.select_location),
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = OutfitFamily
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TextField(
                            value = searchText,
                            onValueChange = { 
                                searchText = it
                                onSearchTextChange(it)
                             },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(id = R.string.enter_location_hint)) },
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    IconButton(onClick = { 
                                        searchText = "" 
                                        onSearchTextChange("")
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.clear))
                                    }
                                }
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )

                        if (predictions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                predictions.take(5).forEach { prediction ->
                                    val cityName = prediction.primaryText
                                    val countryName = prediction.secondaryText
                                    val fullText = if (countryName.isNotEmpty()) "$cityName, $countryName" else cityName

                                    TextButton(
                                        onClick = {
                                            searchText = fullText
                                            onLocationSelected(fullText)
                                            expanded = false
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = fullText,
                                                modifier = Modifier.weight(1f),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    onLocationSelected(null)
                                    expanded = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(id = R.string.clear))
                            }
                            Button(
                                onClick = {
                                    if (searchText.isNotBlank()) {
                                        onLocationSelected(searchText)
                                    } else {
                                        onLocationSelected(null)
                                    }
                                    expanded = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(id = R.string.apply))
                            }
                        }
                    }
                }
            }
        }
    }
}
