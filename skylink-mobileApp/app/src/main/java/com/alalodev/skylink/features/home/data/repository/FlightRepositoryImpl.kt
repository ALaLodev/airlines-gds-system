package com.alalodev.skylink.features.home.data.repository

import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.home.data.model.Flight
import com.alalodev.skylink.features.home.data.remote.FlightApi
import com.alalodev.skylink.features.home.domain.repository.FlightRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepositoryImpl @Inject constructor(
    private val flightApi: FlightApi
) : FlightRepository {

    override suspend fun searchFlights(origin: String, destination: String): NetworkResult<List<Flight>> {
        return try {
            val flights = flightApi.searchFlights(origin, destination)
            NetworkResult.Success(flights)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun getAllFlights(): NetworkResult<List<Flight>> {
        return try {
            val flights = flightApi.getAllFlights()
            NetworkResult.Success(flights)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}
