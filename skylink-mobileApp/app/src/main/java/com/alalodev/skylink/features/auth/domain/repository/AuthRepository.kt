package com.alalodev.skylink.features.auth.domain.repository

import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.auth.data.remote.model.LoginResponse
import com.alalodev.skylink.features.auth.data.remote.model.RegisterResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): NetworkResult<LoginResponse>
    suspend fun register(email: String, password: String): NetworkResult<RegisterResponse>
    suspend fun signInWithGoogle(idToken: String): NetworkResult<RegisterResponse>
    fun saveToken(token: String)
    fun getToken(): String?
    fun getEmail(): String?
    fun logout()
}
