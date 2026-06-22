package com.alalodev.skylink.features.home.data.model

data class Flight(
    val id: Long,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: Double,
    val availableSeats: Int
)
