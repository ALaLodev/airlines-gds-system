package com.alalodev.skylink_kotlinapp.data.remote

import com.alalodev.skylink_kotlinapp.domain.model.LoginRequest
import com.alalodev.skylink_kotlinapp.domain.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>
}
