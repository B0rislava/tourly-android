package com.tourly.app.notifications.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.tourly.app.R
import com.tourly.app.notifications.domain.model.Notification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object NotificationUtils {

    @Composable
    fun getTranslatedTitle(notification: Notification): String {
        val resourceId = when (notification.type) {
            "NEW_BOOKING" -> R.string.notification_new_booking_title
            "BOOKING_CANCELLED" -> R.string.notification_booking_cancelled_title
            "TOUR_CANCELLED" -> R.string.notification_tour_cancelled_title
            else -> null
        }
        return if (resourceId != null) stringResource(id = resourceId) else notification.title
    }

    @Composable
    fun getTranslatedMessage(notification: Notification): String {
        val type = notification.type ?: return notification.message
        val message = notification.message

        return when (type) {
            "NEW_BOOKING" -> {
                // Someone has booked {count} spot(s) for your tour '{title}'.
                val countRegex = "booked (\\d+) spot".toRegex()
                val titleRegex = "your tour '(.*)'\\.".toRegex()
                
                val count = countRegex.find(message)?.groupValues?.get(1)?.toIntOrNull() ?: 1
                val title = titleRegex.find(message)?.groupValues?.get(1) ?: "your tour"
                
                stringResource(id = R.string.notification_new_booking_msg, count, title)
            }
            "BOOKING_CANCELLED" -> {
                if (message.startsWith("Your booking")) {
                    // Your booking for '{title}' has been cancelled.
                    val titleRegex = "for '(.*)' has been".toRegex()
                    val title = titleRegex.find(message)?.groupValues?.get(1) ?: "the tour"
                    stringResource(id = R.string.notification_booking_cancelled_traveler_msg, title)
                } else {
                    // A traveler has cancelled their booking for your tour '{title}'.
                    val titleRegex = "your tour '(.*)'\\.".toRegex()
                    val title = titleRegex.find(message)?.groupValues?.get(1) ?: "your tour"
                    stringResource(id = R.string.notification_booking_cancelled_guide_msg, title)
                }
            }
            "TOUR_CANCELLED" -> {
                // The tour '{title}' has been cancelled by the guide.
                val titleRegex = "The tour '(.*)' has been".toRegex()
                val title = titleRegex.find(message)?.groupValues?.get(1) ?: "the tour"
                stringResource(id = R.string.notification_tour_cancelled_msg, title)
            }
            else -> message
        }
    }

    @Composable
    fun formatNotificationDate(dateTime: LocalDateTime): String {
        val configuration = LocalConfiguration.current
        val locale = configuration.locales[0] ?: Locale.getDefault()
        
        // Feb 01, 20:45 -> 1 февр., 20:45
        val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm", locale)
        return dateTime.format(formatter)
    }
}
