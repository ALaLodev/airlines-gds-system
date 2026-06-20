package com.alalodev.skylink.features.auth.domain.repository

import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.auth.data.remote.model.LoginResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): NetworkResult<LoginResponse>
    fun saveToken(token: String)
    fun getToken(): String?
    fun logout()
}
