package com.tourly.app.home.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.Tag
import com.tourly.app.home.domain.repository.HomeToursRepository
import javax.inject.Inject

class GetAllTagsUseCase @Inject constructor(
    private val repository: HomeToursRepository
) {
    suspend operator fun invoke(): Result<List<Tag>> {
        return repository.getAllTags()
    }
}
