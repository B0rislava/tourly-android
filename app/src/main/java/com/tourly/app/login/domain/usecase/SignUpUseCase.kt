package com.tourly.app.login.domain.usecase

import com.tourly.app.login.domain.repository.AuthRepository
import com.tourly.app.core.network.model.RegisterResponseDto
import com.tourly.app.login.domain.UserRole
import com.tourly.app.core.network.Result
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: UserRole
    ): Result<RegisterResponseDto> {
        return repository.signUp(
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password,
            role = role
        )
    }
}
