package com.alalodev.skylink.features.home.domain.repository

import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.home.data.model.Flight

interface FlightRepository {
    suspend fun searchFlights(origin: String, destination: String): NetworkResult<List<Flight>>
    suspend fun getAllFlights(): NetworkResult<List<Flight>>
}
