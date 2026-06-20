package com.alalodev.skylink_kotlinapp.data.repository

import com.alalodev.skylink_kotlinapp.data.remote.FlightService
import com.alalodev.skylink_kotlinapp.domain.model.Flight
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepository @Inject constructor(
    private val flightService: FlightService
) {
    suspend fun getFlights(page: Int = 0, size: Int = 20): Result<List<Flight>> {
        return try {
            val response = flightService.getFlights(page, size)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFlightById(id: Long): Result<Flight> {
        return try {
            val response = flightService.getFlightById(id)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
