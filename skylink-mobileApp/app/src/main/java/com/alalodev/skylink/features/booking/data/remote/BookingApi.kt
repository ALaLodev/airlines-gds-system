package com.alalodev.skylink.features.booking.data.remote

import retrofit2.http.*

interface BookingApi {
    @GET("api/bookings/flight/{flightId}/seats")
    suspend fun getBookedSeats(
        @Path("flightId") flightId: Long
    ): List<SeatMapDto>

    @POST("api/bookings")
    suspend fun bookFlight(
        @Body request: ReservationRequest
    ): ReservationDto

    @GET("api/auth/users/by-email")
    suspend fun getUserIdByEmail(
        @Query("email") email: String
    ): Long

    @GET("api/bookings/user/{userId}")
    suspend fun getBookingsByUser(
        @Path("userId") userId: Long
    ): List<ReservationDto>
}

data class SeatMapDto(
    val seatNumber: String,
    val cabinClass: String,
    val status: String
)

data class ReservationRequest(
    val userId: Long,
    val scheduleId: Long,
    val totalAmount: Double,
    val seatNumber: String?,
    val cabinClass: String?
)

data class ReservationDto(
    val id: Long,
    val pnr: String,
    val userId: Long,
    val scheduleId: Long,
    val totalAmount: Double,
    val seatNumber: String?,
    val cabinClass: String?,
    val status: String
)
