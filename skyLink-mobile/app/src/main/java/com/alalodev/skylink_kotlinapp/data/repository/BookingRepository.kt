package com.alalodev.skylink_kotlinapp.data.repository

import com.alalodev.skylink_kotlinapp.data.remote.BookingService
import com.alalodev.skylink_kotlinapp.domain.model.Reservation
import com.alalodev.skylink_kotlinapp.domain.model.SeatMapResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val bookingService: BookingService
) {
    suspend fun createReservation(reservation: Reservation): Result<Reservation> {
        return try {
            val response = bookingService.createReservation(reservation)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookedSeats(flightId: Long): Result<List<SeatMapResponse>> {
        return try {
            val response = bookingService.getBookedSeats(flightId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
