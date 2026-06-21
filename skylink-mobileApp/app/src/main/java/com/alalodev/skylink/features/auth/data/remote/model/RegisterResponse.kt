package com.alalodev.skylink.features.auth.data.remote.model

data class RegisterResponse(
    val token: String,
    val message: String? = null
)
