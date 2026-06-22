package com.alalodev.skylink.features.home.data.remote

import com.alalodev.skylink.features.home.data.model.Flight
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightApi {
    @GET("api/flights/search")
    suspend fun searchFlights(
        @Query("origin") origin: String,
        @Query("destination") destination: String
    ): List<Flight>

    @GET("api/flights")
    suspend fun getAllFlights(): List<Flight>
}
