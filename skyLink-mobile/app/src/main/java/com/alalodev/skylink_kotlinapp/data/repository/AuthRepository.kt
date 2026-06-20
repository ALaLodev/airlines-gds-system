package com.alalodev.skylink_kotlinapp.data.repository

import com.alalodev.skylink_kotlinapp.data.local.TokenManager
import com.alalodev.skylink_kotlinapp.data.remote.AuthService
import com.alalodev.skylink_kotlinapp.domain.model.LoginRequest
import com.alalodev.skylink_kotlinapp.domain.model.LoginResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = authService.login(request)
            tokenManager.saveToken(response.data.token)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
}
