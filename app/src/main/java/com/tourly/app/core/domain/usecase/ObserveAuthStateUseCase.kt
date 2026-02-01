package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return userRepository.getTokenFlow()
    }
}
