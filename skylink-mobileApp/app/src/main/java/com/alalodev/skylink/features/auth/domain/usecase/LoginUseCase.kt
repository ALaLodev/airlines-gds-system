package com.alalodev.skylink.features.auth.domain.usecase

import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.auth.data.remote.model.LoginResponse
import com.alalodev.skylink.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): NetworkResult<LoginResponse> {
        return authRepository.login(email, password)
    }
}
