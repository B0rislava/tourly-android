package com.tourly.app.create_tour.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    predictions: List<AutocompletePrediction>,
    onPredictionClick: (AutocompletePrediction) -> Unit,
    placeholder: String,
    error: String? = null
) {
    var showPredictions by remember { mutableStateOf(false) }

    LaunchedEffect(predictions) {
        if (predictions.isNotEmpty()) {
            showPredictions = true
        }
    }



    ExposedDropdownMenuBox(
        expanded = showPredictions,
        onExpandedChange = { expanded -> 
            showPredictions = expanded
        },
        modifier = modifier
    ) {
        CustomTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                showPredictions = it.length >= 2
            },
            placeholder = placeholder,
            error = error,
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = showPredictions && predictions.isNotEmpty(),
            onDismissRequest = { showPredictions = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            predictions.forEach { prediction ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = prediction.getPrimaryText(null).toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = OutfitFamily
                            )
                            Text(
                                text = prediction.getSecondaryText(null).toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = OutfitFamily
                            )
                        }
                    },
                    onClick = {
                        onPredictionClick(prediction)
                        showPredictions = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}