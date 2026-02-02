package com.tourly.app.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tourly.app.R

object StatusUtils {
    
    @Composable
    fun getTranslatedStatus(status: String): String {
        val resourceId = when (status.uppercase()) {
            "CONFIRMED" -> R.string.status_confirmed
            "CANCELLED" -> R.string.status_cancelled
            "COMPLETED" -> R.string.status_completed
            "PENDING" -> R.string.status_pending
            else -> null
        }
        return if (resourceId != null) stringResource(id = resourceId) else status
    }
}
