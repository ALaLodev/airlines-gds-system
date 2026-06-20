package com.alalodev.skylink_kotlinapp.di

import com.alalodev.skylink_kotlinapp.data.repository.FlightRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepositoryEntryPoint {
    fun flightRepository(): FlightRepository
}
