package com.alalodev.skylink.features.home.domain.usecase

import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.home.data.model.Flight
import com.alalodev.skylink.features.home.domain.repository.FlightRepository
import javax.inject.Inject

class SearchFlightsUseCase @Inject constructor(
    private val flightRepository: FlightRepository
) {
    suspend operator fun invoke(origin: String, destination: String): NetworkResult<List<Flight>> {
        if (origin.isBlank() || destination.isBlank()) {
            return NetworkResult.Error(IllegalArgumentException("Origin and destination codes must not be empty"))
        }
        return flightRepository.searchFlights(origin, destination)
    }
}
