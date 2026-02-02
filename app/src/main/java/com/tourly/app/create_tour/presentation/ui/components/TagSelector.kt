package com.tourly.app.create_tour.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tourly.app.core.presentation.util.TagUtils
import androidx.compose.ui.unit.dp
import com.tourly.app.core.domain.model.Tag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelector(
    availableTags: List<Tag>,
    selectedTagIds: Set<Long>,
    onTagToggled: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        availableTags.forEach { tag ->
            val isSelected = tag.id in selectedTagIds
            FilterChip(
                selected = isSelected,
                onClick = { onTagToggled(tag.id) },
                label = { Text(TagUtils.getTranslatedName(tag)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurface 
                )
            )
        }
    }
}
