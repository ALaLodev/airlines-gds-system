package com.alalodev.skylink.core.di

import com.alalodev.skylink.features.home.data.remote.FlightApi
import com.alalodev.skylink.features.home.data.repository.FlightRepositoryImpl
import com.alalodev.skylink.features.home.domain.repository.FlightRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    @Singleton
    abstract fun bindFlightRepository(flightRepositoryImpl: FlightRepositoryImpl): FlightRepository

    companion object {
        @Provides
        @Singleton
        fun provideFlightApi(retrofit: Retrofit): FlightApi = retrofit.create(FlightApi::class.java)
    }
}
