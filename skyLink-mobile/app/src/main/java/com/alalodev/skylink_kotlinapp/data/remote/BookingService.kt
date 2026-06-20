package com.alalodev.skylink_kotlinapp.data.remote

import com.alalodev.skylink_kotlinapp.domain.model.Reservation
import com.alalodev.skylink_kotlinapp.domain.model.SeatMapResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingService {
    @POST("api/bookings")
    suspend fun createReservation(@Body reservation: Reservation): ApiResponse<Reservation>

    @GET("api/bookings/flight/{flightId}/seats")
    suspend fun getBookedSeats(@Path("flightId") flightId: Long): List<SeatMapResponse>
}
