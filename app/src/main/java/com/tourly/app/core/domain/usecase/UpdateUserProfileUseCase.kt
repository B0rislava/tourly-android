package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.profile.data.dto.UpdateProfileRequestDto
import com.tourly.app.core.domain.model.User
import javax.inject.Inject
import com.tourly.app.core.network.Result

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(request: UpdateProfileRequestDto): Result<User> {
        return repository.updateUserProfile(request)
    }
}
