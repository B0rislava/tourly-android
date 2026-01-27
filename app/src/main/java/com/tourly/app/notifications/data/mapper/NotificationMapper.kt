package com.tourly.app.notifications.data.mapper

import com.tourly.app.notifications.data.remote.dto.NotificationDto
import com.tourly.app.notifications.domain.model.Notification
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NotificationDto.toDomain(): Notification {
    return Notification(
        id = id,
        title = title,
        message = message,
        isRead = isRead,
        createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME),
        type = type,
        relatedId = relatedId
    )
}
