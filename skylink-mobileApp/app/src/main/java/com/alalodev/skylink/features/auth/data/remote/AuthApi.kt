package com.alalodev.skylink.features.auth.data.remote

import com.alalodev.skylink.features.auth.data.remote.model.LoginRequest
import com.alalodev.skylink.features.auth.data.remote.model.LoginResponse
import com.alalodev.skylink.features.auth.data.remote.model.RegisterRequest
import com.alalodev.skylink.features.auth.data.remote.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}
