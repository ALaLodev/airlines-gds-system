package com.alalodev.skylink.features.auth.presentation

import com.alalodev.skylink.features.auth.data.remote.model.LoginResponse

data class AuthState(
    val isLoading: Boolean = false,
    val loginResponse: LoginResponse? = null,
    val error: String? = null
)
