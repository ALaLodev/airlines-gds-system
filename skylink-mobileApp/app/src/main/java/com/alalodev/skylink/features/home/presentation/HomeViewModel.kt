package com.alalodev.skylink.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.home.data.model.Flight
import com.alalodev.skylink.features.home.domain.usecase.SearchFlightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchFlightsUseCase: SearchFlightsUseCase
) : ViewModel() {

    // Lista de aeropuertos disponibles
    val airports = listOf(
        AirportItem("BCN", "Barcelona (BCN)", "España"),
        AirportItem("MAD", "Madrid (MAD)", "España"),
        AirportItem("CDG", "París CDG (CDG)", "Francia"),
        AirportItem("FCO", "Roma Fiumicino (FCO)", "Italia"),
        AirportItem("NRT", "Tokio Narita (NRT)", "Japón"),
        AirportItem("ZRH", "Zúrich (ZRH)", "Suiza"),
        AirportItem("JFK", "Nueva York (JFK)", "EE. UU."),
        AirportItem("LHR", "Londres Heathrow (LHR)", "Reino Unido"),
        AirportItem("DXB", "Dubái (DXB)", "EAU"),
        AirportItem("MIA", "Miami (MIA)", "EE. UU."),
        AirportItem("LAX", "Los Ángeles (LAX)", "EE. UU.")
    )

    // Formateador de fecha
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

    // Estados de búsqueda
    private val _tripType = MutableStateFlow(0) // 0 = Round Trip, 1 = One Way
    val tripType: StateFlow<Int> = _tripType.asStateFlow()

    private val _origin = MutableStateFlow(airports[1]) // Por defecto Madrid (MAD)
    val origin: StateFlow<AirportItem> = _origin.asStateFlow()

    private val _destination = MutableStateFlow(airports[0]) // Por defecto Barcelona (BCN)
    val destination: StateFlow<AirportItem> = _destination.asStateFlow()

    private val _departureDate = MutableStateFlow(LocalDate.now().plusDays(1))
    val departureDate: StateFlow<LocalDate> = _departureDate.asStateFlow()

    private val _returnDate = MutableStateFlow(LocalDate.now().plusDays(8))
    val returnDate: StateFlow<LocalDate> = _returnDate.asStateFlow()

    private val _adultsCount = MutableStateFlow(1)
    val adultsCount: StateFlow<Int> = _adultsCount.asStateFlow()

    private val _childrenCount = MutableStateFlow(0)
    val childrenCount: StateFlow<Int> = _childrenCount.asStateFlow()

    // Estados de diálogos / popups
    private val _showOriginSelector = MutableStateFlow(false)
    val showOriginSelector: StateFlow<Boolean> = _showOriginSelector.asStateFlow()

    private val _showDestinationSelector = MutableStateFlow(false)
    val showDestinationSelector: StateFlow<Boolean> = _showDestinationSelector.asStateFlow()

    private val _showTravelerSelector = MutableStateFlow(false)
    val showTravelerSelector: StateFlow<Boolean> = _showTravelerSelector.asStateFlow()

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker.asStateFlow()

    // Estados del resultado de la búsqueda
    private val _searchResults = MutableStateFlow<NetworkResult<List<Flight>>>(NetworkResult.Success(emptyList()))
    val searchResults: StateFlow<NetworkResult<List<Flight>>> = _searchResults.asStateFlow()

    // Métodos para cambiar estados
    fun setTripType(type: Int) {
        _tripType.value = type
    }

    fun setOrigin(airport: AirportItem) {
        _origin.value = airport
        _showOriginSelector.value = false
    }

    fun setDestination(airport: AirportItem) {
        _destination.value = airport
        _showDestinationSelector.value = false
    }

    fun swapAirports() {
        val temp = _origin.value
        _origin.value = _destination.value
        _destination.value = temp
    }

    fun setDepartureDate(date: LocalDate) {
        _departureDate.value = date
        _showDatePicker.value = false
        // Si la fecha de regreso es anterior a la nueva de salida, la empujamos
        if (_returnDate.value.isBefore(date)) {
            _returnDate.value = date.plusDays(7)
        }
    }

    fun setReturnDate(date: LocalDate) {
        _returnDate.value = date
        // Si es antes de la de salida, ajustamos la de salida
        if (date.isBefore(_departureDate.value)) {
            _departureDate.value = date.minusDays(7)
        }
    }

    fun setDates(startDate: LocalDate, endDate: LocalDate) {
        _departureDate.value = startDate
        _returnDate.value = endDate
        _showDatePicker.value = false
    }

    fun incrementAdults() {
        if (_adultsCount.value < 9) {
            _adultsCount.value += 1
        }
    }

    fun decrementAdults() {
        if (_adultsCount.value > 1) {
            _adultsCount.value -= 1
        }
    }

    fun incrementChildren() {
        if (_childrenCount.value < 9) {
            _childrenCount.value += 1
        }
    }

    fun decrementChildren() {
        if (_childrenCount.value > 0) {
            _childrenCount.value -= 1
        }
    }

    fun toggleOriginSelector(show: Boolean) {
        _showOriginSelector.value = show
    }

    fun toggleDestinationSelector(show: Boolean) {
        _showDestinationSelector.value = show
    }

    fun toggleTravelerSelector(show: Boolean) {
        _showTravelerSelector.value = show
    }

    fun toggleDatePicker(show: Boolean) {
        _showDatePicker.value = show
    }

    // Retorna la fecha de salida formateada en texto
    fun getFormattedDepartureDate(): String = _departureDate.value.format(dateFormatter)

    // Retorna la fecha de regreso formateada en texto
    fun getFormattedReturnDate(): String = _returnDate.value.format(dateFormatter)

    // Retorna el texto para Travelers
    fun getFormattedTravelersText(): String {
        val adults = _adultsCount.value
        val children = _childrenCount.value
        val adultsText = if (adults == 1) "1 Adulto" else "$adults Adultos"
        val childrenText = when (children) {
            0 -> ""
            1 -> ", 1 Niño"
            else -> ", $children Niños"
        }
        return "$adultsText$childrenText"
    }

    // Cargar resultados de vuelo en la pantalla de resultados
    fun searchFlights(originCode: String, destinationCode: String) {
        _searchResults.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = searchFlightsUseCase(originCode, destinationCode)
            _searchResults.value = result
        }
    }
}

data class AirportItem(
    val code: String,
    val name: String,
    val country: String
)
