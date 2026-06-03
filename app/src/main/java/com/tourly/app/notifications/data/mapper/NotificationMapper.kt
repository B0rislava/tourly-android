package com.tourly.app.notifications.data.mapper

import com.tourly.app.core.util.DateUtils
import com.tourly.app.notifications.data.remote.dto.NotificationDto
import com.tourly.app.notifications.domain.model.Notification

fun NotificationDto.toDomain(): Notification {
    return Notification(
        id = id,
        title = title,
        message = message,
        isRead = isRead,
        createdAt = DateUtils.parseUtcToLocal(createdAt),
        type = type,
        relatedId = relatedId
    )
}
