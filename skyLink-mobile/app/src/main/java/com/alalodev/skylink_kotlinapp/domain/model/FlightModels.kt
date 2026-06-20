package com.alalodev.skylink_kotlinapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Flight(
    val id: Long,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: Double,
    val availableSeats: Int
)

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val currentPage: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
)
