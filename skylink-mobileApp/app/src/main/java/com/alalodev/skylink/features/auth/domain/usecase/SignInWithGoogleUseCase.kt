package com.alalodev.skylink.features.auth.domain.usecase

import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.auth.data.remote.model.RegisterResponse
import com.alalodev.skylink.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): NetworkResult<RegisterResponse> {
        return authRepository.signInWithGoogle(idToken)
    }
}
