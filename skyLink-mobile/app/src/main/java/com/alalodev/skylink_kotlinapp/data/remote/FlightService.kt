package com.alalodev.skylink_kotlinapp.data.remote

import com.alalodev.skylink_kotlinapp.domain.model.Flight
import com.alalodev.skylink_kotlinapp.domain.model.PaginatedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FlightService {
    @GET("api/flights")
    suspend fun getFlights(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PaginatedResponse<Flight>

    @GET("api/flights/{id}")
    suspend fun getFlightById(@Path("id") id: Long): ApiResponse<Flight>
}
