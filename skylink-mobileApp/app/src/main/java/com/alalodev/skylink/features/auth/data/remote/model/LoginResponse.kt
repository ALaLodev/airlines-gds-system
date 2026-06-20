package com.alalodev.skylink.features.auth.data.remote.model

data class LoginResponseWrapper(
    val data: LoginResponse? = null,
    val message: String? = null,
    val status: Int? = null
)

data class LoginResponse(
    val token: String,
    val email: String,
    val roles: List<String>
)
