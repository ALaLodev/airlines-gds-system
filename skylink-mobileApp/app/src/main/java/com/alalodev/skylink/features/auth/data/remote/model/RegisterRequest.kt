package com.alalodev.skylink.features.auth.data.remote.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val role: String = "ROLE_CUSTOMER"
)
