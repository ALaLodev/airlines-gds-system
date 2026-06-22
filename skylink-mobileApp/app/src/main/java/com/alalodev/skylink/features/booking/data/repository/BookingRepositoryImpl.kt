package com.alalodev.skylink.features.booking.data.repository

import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.booking.data.remote.BookingApi
import com.alalodev.skylink.features.booking.data.remote.ReservationDto
import com.alalodev.skylink.features.booking.data.remote.ReservationRequest
import com.alalodev.skylink.features.booking.data.remote.SeatMapDto
import com.alalodev.skylink.features.booking.domain.repository.BookingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val bookingApi: BookingApi
) : BookingRepository {

    override suspend fun getBookedSeats(flightId: Long): NetworkResult<List<SeatMapDto>> {
        return try {
            val seats = bookingApi.getBookedSeats(flightId)
            NetworkResult.Success(seats)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun bookFlight(
        userId: Long,
        scheduleId: Long,
        totalAmount: Double,
        seatNumber: String?,
        cabinClass: String?
    ): NetworkResult<ReservationDto> {
        return try {
            val request = ReservationRequest(
                userId = userId,
                scheduleId = scheduleId,
                totalAmount = totalAmount,
                seatNumber = seatNumber,
                cabinClass = cabinClass
            )
            val result = bookingApi.bookFlight(request)
            NetworkResult.Success(result)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun getUserIdByEmail(email: String): NetworkResult<Long> {
        return try {
            val userId = bookingApi.getUserIdByEmail(email)
            NetworkResult.Success(userId)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun getBookingsByUser(userId: Long): NetworkResult<List<ReservationDto>> {
        return try {
            val bookings = bookingApi.getBookingsByUser(userId)
            NetworkResult.Success(bookings)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}
