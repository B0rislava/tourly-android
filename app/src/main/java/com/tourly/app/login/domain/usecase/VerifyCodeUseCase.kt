package com.tourly.app.login.domain.usecase

import com.tourly.app.login.domain.repository.AuthRepository
import com.tourly.app.core.network.Result
import com.tourly.app.login.data.dto.LoginResponseDto
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, code: String): Result<LoginResponseDto> {
        return repository.verifyCode(email, code)
    }
}
