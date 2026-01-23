package com.tourly.app.core.presentation.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatters {
    fun formatDate(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString)
            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)
            date.format(formatter)
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatDuration(durationString: String): String {
        // format "HH:mm"
        return try {
            val parts = durationString.split(":")
            if (parts.size >= 2) {
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts[1].toIntOrNull() ?: 0

                val sb = StringBuilder()
                if (hours > 0) {
                    sb.append("${hours}h")
                }
                if (minutes > 0) {
                    if (sb.isNotEmpty()) sb.append(" ")
                    sb.append("${minutes}m")
                }
                if (sb.isEmpty()) "0m" else sb.toString()
            } else {
                durationString
            }
        } catch (e: Exception) {
            durationString
        }
    }
}
