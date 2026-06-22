package com.alalodev.skylink.features.booking.domain.repository

import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.booking.data.remote.ReservationDto
import com.alalodev.skylink.features.booking.data.remote.SeatMapDto

interface BookingRepository {
    suspend fun getBookedSeats(flightId: Long): NetworkResult<List<SeatMapDto>>
    suspend fun bookFlight(
        userId: Long,
        scheduleId: Long,
        totalAmount: Double,
        seatNumber: String?,
        cabinClass: String?
    ): NetworkResult<ReservationDto>
    suspend fun getUserIdByEmail(email: String): NetworkResult<Long>
    suspend fun getBookingsByUser(userId: Long): NetworkResult<List<ReservationDto>>
}
