package com.tourly.app.core.network.mapper

import com.tourly.app.home.data.dto.TagDto
import com.tourly.app.core.domain.model.Tag

object TagMapper {
    fun toDomain(dto: TagDto): Tag {
        return Tag(
            id = dto.id,
            name = dto.name,
            displayName = dto.displayName
        )
    }
}
