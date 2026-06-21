package com.alalodev.skylink.features.auth.data.remote.model

data class LoginResponse(
    val token: String,
    val message: String? = null,
    val email: String? = null,
    val roles: List<String>? = null
)
