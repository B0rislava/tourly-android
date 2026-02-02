package com.tourly.app.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tourly.app.R
import com.tourly.app.core.domain.model.Tag

object TagUtils {

    // Map a tag name to its translated string resource.
    // If no translation is found, return the tag's displayName.

    @Composable
    fun getTranslatedName(tag: Tag): String {
        val resourceId = when (tag.name.lowercase()) {
            "adventure" -> R.string.tag_adventure
            "nature" -> R.string.tag_nature
            "historical" -> R.string.tag_historical
            "urban" -> R.string.tag_urban
            "food" -> R.string.tag_food
            "hiking" -> R.string.tag_hiking
            "culture" -> R.string.tag_culture
            "architecture" -> R.string.tag_architecture
            "nightlife" -> R.string.tag_nightlife
            "photography" -> R.string.tag_photography
            else -> null
        }
        
        return if (resourceId != null) {
            stringResource(id = resourceId)
        } else {
            tag.displayName
        }
    }
}
