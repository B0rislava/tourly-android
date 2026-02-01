package com.tourly.app.login.domain.usecase

import com.tourly.app.login.domain.repository.AuthRepository
import com.tourly.app.login.domain.UserRole
import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.User
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
    ): Result<User> {
        return repository.signUp(
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password,
            role = role
        )
    }
}
