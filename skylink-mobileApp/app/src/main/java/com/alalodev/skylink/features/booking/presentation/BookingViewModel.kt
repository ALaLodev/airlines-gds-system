package com.alalodev.skylink.features.booking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.auth.domain.repository.AuthRepository
import com.alalodev.skylink.features.booking.data.remote.ReservationDto
import com.alalodev.skylink.features.booking.domain.repository.BookingRepository
import com.alalodev.skylink.features.home.data.model.Flight
import com.alalodev.skylink.features.home.domain.repository.FlightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

// Asientos de turista disponibles para auto-asignación (filas 4-20, columnas A-F)
private val ECONOMY_SEATS = (4..20).flatMap { row ->
    listOf("${row}A", "${row}B", "${row}C", "${row}D", "${row}E", "${row}F")
}

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val flightRepository: FlightRepository,
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _flightState = MutableStateFlow<NetworkResult<Flight>>(NetworkResult.Loading())
    val flightState: StateFlow<NetworkResult<Flight>> = _flightState.asStateFlow()

    val userEmail: String? = authRepository.getEmail()
    val passengerName: String = userEmail?.let { formatEmailToName(it) } ?: "Alexander Mitchell"

    private val _userIdState = MutableStateFlow<Long?>(null)
    val userIdState: StateFlow<Long?> = _userIdState.asStateFlow()

    private val _bookedSeatsState = MutableStateFlow<List<String>>(emptyList())
    val bookedSeatsState: StateFlow<List<String>> = _bookedSeatsState.asStateFlow()

    private val _bookingResultState = MutableStateFlow<NetworkResult<ReservationDto>?>(null)
    val bookingResultState: StateFlow<NetworkResult<ReservationDto>?> = _bookingResultState.asStateFlow()

    private val _userBookingsState = MutableStateFlow<NetworkResult<List<UserBooking>>>(NetworkResult.Loading())
    val userBookingsState: StateFlow<NetworkResult<List<UserBooking>>> = _userBookingsState.asStateFlow()

    init {
        loadUserId()
    }

    private fun loadUserId() {
        val email = userEmail ?: return
        viewModelScope.launch {
            when (val result = bookingRepository.getUserIdByEmail(email)) {
                is NetworkResult.Success -> {
                    _userIdState.value = result.data
                }
                else -> {
                    // Si falla la petición, dejamos el userId a null y se reintentará en loadUserBookings
                    _userIdState.value = null
                }
            }
        }
    }

    fun loadUserBookings() {
        val email = userEmail ?: run {
            _userBookingsState.value = NetworkResult.Error(Exception("No hay sesión iniciada"))
            return
        }
        _userBookingsState.value = NetworkResult.Loading()
        viewModelScope.launch {
            // 1. Intentar usar el userId ya cacheado, o cargarlo si aún no está disponible
            val cachedUserId = _userIdState.value
            val userId: Long
            if (cachedUserId != null) {
                userId = cachedUserId
            } else {
                // Reintento de carga del userId
                val userIdResult = bookingRepository.getUserIdByEmail(email)
                if (userIdResult is NetworkResult.Error) {
                    _userBookingsState.value = NetworkResult.Error(
                        Exception("No se pudo identificar al usuario. Verifica tu conexión.")
                    )
                    return@launch
                }
                userId = (userIdResult as NetworkResult.Success).data
                _userIdState.value = userId
            }

            // 2. Fetch bookings y todos los vuelos en paralelo
            val bookingsResult = bookingRepository.getBookingsByUser(userId)
            val flightsResult = flightRepository.getAllFlights()

            if (bookingsResult is NetworkResult.Success && flightsResult is NetworkResult.Success) {
                val bookings = bookingsResult.data
                val flights = flightsResult.data

                val userBookings = bookings.map { booking ->
                    val flight = flights.find { it.id == booking.scheduleId }
                    UserBooking(
                        id = booking.id,
                        pnr = booking.pnr,
                        userId = booking.userId,
                        scheduleId = booking.scheduleId,
                        totalAmount = booking.totalAmount,
                        seatNumber = booking.seatNumber,
                        cabinClass = booking.cabinClass,
                        status = booking.status,
                        flight = flight
                    )
                }
                _userBookingsState.value = NetworkResult.Success(userBookings.sortedByDescending { it.id })
            } else {
                val bookingError = (bookingsResult as? NetworkResult.Error)?.exception
                val flightError = (flightsResult as? NetworkResult.Error)?.exception
                val errorMessage = when {
                    bookingError != null -> "Error al cargar reservas: ${bookingError.message}"
                    flightError != null -> "Error al cargar vuelos: ${flightError.message}"
                    else -> "Error desconocido al cargar tus viajes"
                }
                _userBookingsState.value = NetworkResult.Error(Exception(errorMessage))
            }
        }
    }

    fun loadFlight(flightId: String) {
        _flightState.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = flightRepository.getAllFlights()
            when (result) {
                is NetworkResult.Success -> {
                    val flight = result.data.find { it.id.toString() == flightId }
                    if (flight != null) {
                        _flightState.value = NetworkResult.Success(flight)
                    } else {
                        _flightState.value = NetworkResult.Error(Exception("Flight not found"))
                    }
                }
                is NetworkResult.Error -> {
                    _flightState.value = NetworkResult.Error(result.exception)
                }
                is NetworkResult.Loading -> {
                    _flightState.value = NetworkResult.Loading()
                }
            }
        }
    }

    fun loadBookedSeats(flightId: String) {
        val id = flightId.toLongOrNull() ?: return
        viewModelScope.launch {
            when (val result = bookingRepository.getBookedSeats(id)) {
                is NetworkResult.Success -> {
                    _bookedSeatsState.value = result.data.map { it.seatNumber }
                }
                else -> {
                    _bookedSeatsState.value = emptyList()
                }
            }
        }
    }

    fun bookFlight(flightId: String, totalAmount: Double, seatNumber: String?, cabinClass: String?) {
        val id = flightId.toLongOrNull() ?: return
        val userId = _userIdState.value ?: 1L
        _bookingResultState.value = NetworkResult.Loading()
        viewModelScope.launch {
            // Si no eligió asiento, asignamos uno aleatorio de turista
            // descartando los ya ocupados
            val finalSeat: String?
            val finalCabin: String?
            if (seatNumber == null || seatNumber == "none") {
                val occupiedSeats = _bookedSeatsState.value.toSet()
                val availableSeats = ECONOMY_SEATS.filter { it !in occupiedSeats }
                finalSeat = availableSeats.randomOrNull() ?: ECONOMY_SEATS.random()
                finalCabin = "ECONOMY"
            } else {
                finalSeat = seatNumber
                finalCabin = cabinClass
            }

            val result = bookingRepository.bookFlight(
                userId = userId,
                scheduleId = id,
                totalAmount = totalAmount,
                seatNumber = finalSeat,
                cabinClass = finalCabin
            )
            _bookingResultState.value = result
        }
    }

    fun clearBookingResult() {
        _bookingResultState.value = null
    }

    private fun formatEmailToName(email: String): String {
        val localPart = email.substringBefore('@')
        return localPart.split('.', '_', '-')
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString()
                }
            }
    }
}

data class UserBooking(
    val id: Long,
    val pnr: String,
    val userId: Long,
    val scheduleId: Long,
    val totalAmount: Double,
    val seatNumber: String?,
    val cabinClass: String?,
    val status: String,
    val flight: Flight?
)
