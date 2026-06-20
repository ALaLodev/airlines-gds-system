package com.alalodev.skylink_kotlinapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Reservation(
    val id: Long? = null,
    val pnr: String? = null,
    val userId: Long,
    val scheduleId: Long,
    val totalAmount: Double,
    val seatNumber: String? = null,
    val cabinClass: String? = null,
    val status: String = "PENDING",
    val createdAt: String? = null
)

@Serializable
data class SeatMapResponse(
    val seatNumber: String,
    val isBooked: Boolean
)
