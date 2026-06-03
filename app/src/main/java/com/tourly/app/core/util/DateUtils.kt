package com.tourly.app.core.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

object DateUtils {
    fun parseUtcToLocal(dateString: String): LocalDateTime {
        return try {
            // Try parsing as ZonedDateTime
            ZonedDateTime.parse(dateString).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        } catch (e: Exception) {
            try {
                LocalDateTime.parse(dateString).atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            } catch (e: Exception) {
                LocalDateTime.now()
            }
        }
    }
}
