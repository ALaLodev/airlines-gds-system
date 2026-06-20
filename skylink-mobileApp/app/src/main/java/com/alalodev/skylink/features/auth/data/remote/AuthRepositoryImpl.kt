package com.alalodev.skylink.features.auth.data.remote

import android.content.SharedPreferences
import com.alalodev.skylink.core.network.AuthInterceptor
import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.auth.data.remote.model.LoginRequest
import com.alalodev.skylink.features.auth.data.remote.model.LoginResponse
import com.alalodev.skylink.features.auth.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sharedPreferences: SharedPreferences,
    private val authInterceptor: AuthInterceptor
) : AuthRepository {

    override suspend fun login(email: String, password: String): NetworkResult<LoginResponse> {
        // Mocking login for testing UI
        if (email == "test@skylink.com" && password == "admin123") {
            val mockResponse = LoginResponse(
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock_token",
                email = email,
                roles = listOf("ROLE_PASSENGER")
            )
            saveToken(mockResponse.token)
            return NetworkResult.Success(mockResponse)
        }

        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.status == 200 && response.data != null) {
                saveToken(response.data.token)
                NetworkResult.Success(response.data)
            } else {
                NetworkResult.Error(Exception(response.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override fun saveToken(token: String) {
        sharedPreferences.edit().putString("jwt_token", token).apply()
        authInterceptor.setToken(token)
    }

    override fun getToken(): String? {
        val token = sharedPreferences.getString("jwt_token", null)
        authInterceptor.setToken(token)
        return token
    }

    override fun logout() {
        sharedPreferences.edit().remove("jwt_token").apply()
        authInterceptor.setToken(null)
    }
}
