package com.alalodev.skylink_kotlinapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val type: String = "Bearer",
    val email: String,
    val roles: List<String>
)
