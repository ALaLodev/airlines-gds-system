package com.alalodev.skylink_kotlinapp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T
)
