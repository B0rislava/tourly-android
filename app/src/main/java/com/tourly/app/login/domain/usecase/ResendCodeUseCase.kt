package com.tourly.app.login.domain.usecase

import com.tourly.app.login.domain.repository.AuthRepository
import com.tourly.app.core.network.Result
import javax.inject.Inject

class ResendCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.resendCode(email)
    }
}
