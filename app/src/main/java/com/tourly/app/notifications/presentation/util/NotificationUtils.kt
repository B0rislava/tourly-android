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
            "BOOKING_CANCELLED",
            "BOOKING_CANCELLED_TRAVELER",
            "BOOKING_CANCELLED_GUIDE" -> R.string.notification_booking_cancelled_title
            "TOUR_CANCELLED" -> R.string.notification_tour_cancelled_title
            "FOLLOW" -> R.string.notification_follow_title
            "NEW_TOUR" -> R.string.notification_new_tour_title
            else -> null
        }
        return if (resourceId != null) stringResource(id = resourceId) else notification.title
    }

    @Composable
    fun getTranslatedMessage(notification: Notification): String {
        val type = notification.type ?: return notification.message
        val parts = notification.message.split("|")
        
        return when (type) {
            "NEW_BOOKING" -> {
                // name|count|title
                if (parts.size >= 3) {
                    stringResource(id = R.string.notification_new_booking_msg, parts[0], parts[1], parts[2])
                } else notification.message
            }
            "BOOKING_CANCELLED_TRAVELER" -> {
                // title
                stringResource(id = R.string.notification_booking_cancelled_traveler_msg, parts.getOrElse(0){""})
            }
            "BOOKING_CANCELLED_GUIDE" -> {
                // name|title
                if (parts.size >= 2) {
                    stringResource(id = R.string.notification_booking_cancelled_guide_msg, parts[0], parts[1])
                } else notification.message
            }
            "TOUR_CANCELLED" -> {
                // title
                stringResource(id = R.string.notification_tour_cancelled_msg, parts.getOrElse(0){""})
            }
            "FOLLOW" -> {
                // name
                stringResource(id = R.string.notification_follow_msg, parts.getOrElse(0){""})
            }
            "NEW_TOUR" -> {
                // name|title
                if (parts.size >= 2) {
                    stringResource(id = R.string.notification_new_tour_msg, parts[0], parts[1])
                } else notification.message
            }
            else -> notification.message
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
