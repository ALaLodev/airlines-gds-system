package com.alalodev.skylink_kotlinapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alalodev.skylink_kotlinapp.data.repository.FlightRepository
import com.alalodev.skylink_kotlinapp.domain.model.Flight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val flightRepository: FlightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadFlights()
    }

    fun loadFlights() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = flightRepository.getFlights()
            if (result.isSuccess) {
                _uiState.value = HomeUiState.Success(result.getOrDefault(emptyList()))
            } else {
                _uiState.value = HomeUiState.Error(result.exceptionOrNull()?.message ?: "Error loading flights")
            }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val flights: List<Flight>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
