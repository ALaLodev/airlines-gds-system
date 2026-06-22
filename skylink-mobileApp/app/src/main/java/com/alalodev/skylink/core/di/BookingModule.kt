package com.alalodev.skylink.core.di

import com.alalodev.skylink.features.booking.data.remote.BookingApi
import com.alalodev.skylink.features.booking.data.repository.BookingRepositoryImpl
import com.alalodev.skylink.features.booking.domain.repository.BookingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BookingModule {

    @Binds
    @Singleton
    abstract fun bindBookingRepository(bookingRepositoryImpl: BookingRepositoryImpl): BookingRepository

    companion object {
        @Provides
        @Singleton
        fun provideBookingApi(retrofit: Retrofit): BookingApi = retrofit.create(BookingApi::class.java)
    }
}
