package com.tourly.app.login.domain.usecase

import com.tourly.app.core.network.model.RegisterResponse
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.domain.repository.AuthRepository
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
    ): Result<RegisterResponse> {
        return repository.signUp(
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password,
            role = role
        )
    }
}
