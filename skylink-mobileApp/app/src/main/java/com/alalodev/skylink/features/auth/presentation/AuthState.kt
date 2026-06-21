package com.alalodev.skylink.features.auth.presentation

import com.alalodev.skylink.features.auth.data.remote.model.LoginResponse
import com.alalodev.skylink.features.auth.data.remote.model.RegisterResponse

data class AuthState(
    val isLoading: Boolean = false,
    val loginResponse: LoginResponse? = null,
    val registerResponse: RegisterResponse? = null,
    val registerSuccess: Boolean = false,
    val error: String? = null
)
