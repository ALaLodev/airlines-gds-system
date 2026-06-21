package com.alalodev.skylink.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.auth.domain.usecase.LoginUseCase
import com.alalodev.skylink.features.auth.domain.usecase.RegisterUseCase
import com.alalodev.skylink.features.auth.domain.usecase.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = loginUseCase(email, password)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    _state.update { it.copy(isLoading = false, loginResponse = data) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message ?: "Unknown error") }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, registerSuccess = false) }
            when (val result = registerUseCase(email, password)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    _state.update { it.copy(isLoading = false, registerResponse = data, registerSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message ?: "Registration failed") }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, registerSuccess = false) }
            when (val result = signInWithGoogleUseCase(idToken)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    // Since registering/logging in via Google logs the user in successfully,
                    // we can update registerSuccess = true to navigate them forward.
                    _state.update { it.copy(isLoading = false, registerResponse = data, registerSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message ?: "Google Sign-In failed") }
                }
                is NetworkResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetRegisterState() {
        _state.update { it.copy(registerSuccess = false, registerResponse = null) }
    }
}
