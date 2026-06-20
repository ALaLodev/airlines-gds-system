package com.alalodev.skylink_kotlinapp.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alalodev.skylink_kotlinapp.data.repository.BookingRepository
import com.alalodev.skylink_kotlinapp.domain.model.Flight
import com.alalodev.skylink_kotlinapp.domain.model.Reservation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun createReservation(flight: Flight, seatNumber: String, cabinClass: String, userId: Long) {
        viewModelScope.launch {
            _uiState.value = BookingUiState.Loading
            
            val reservationRequest = Reservation(
                userId = userId,
                scheduleId = flight.id,
                totalAmount = flight.price,
                seatNumber = seatNumber,
                cabinClass = cabinClass,
                status = "PENDING"
            )
            
            val result = bookingRepository.createReservation(reservationRequest)
            
            if (result.isSuccess) {
                _uiState.value = BookingUiState.Success(result.getOrThrow())
            } else {
                _uiState.value = BookingUiState.Error(result.exceptionOrNull()?.message ?: "Error al crear la reserva")
            }
        }
    }
}

sealed class BookingUiState {
    object Idle : BookingUiState()
    object Loading : BookingUiState()
    data class Success(val reservation: Reservation) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}
