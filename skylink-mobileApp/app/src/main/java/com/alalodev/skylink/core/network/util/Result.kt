package com.alalodev.skylink.core.network.util

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val exception: Throwable, val message: String? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}

// Dummy to avoid empty file or similar issues if any
val dummy = 1
