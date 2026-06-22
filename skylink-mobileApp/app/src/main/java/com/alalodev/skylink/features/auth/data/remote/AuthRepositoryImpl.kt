package com.alalodev.skylink.features.auth.data.remote

import android.content.SharedPreferences
import com.alalodev.skylink.core.network.AuthInterceptor
import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.auth.data.remote.model.LoginRequest
import com.alalodev.skylink.features.auth.data.remote.model.LoginResponse
import com.alalodev.skylink.features.auth.data.remote.model.RegisterRequest
import com.alalodev.skylink.features.auth.data.remote.model.RegisterResponse
import com.alalodev.skylink.features.auth.domain.repository.AuthRepository
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sharedPreferences: SharedPreferences,
    private val authInterceptor: AuthInterceptor
) : AuthRepository {

    override suspend fun login(email: String, password: String): NetworkResult<LoginResponse> {
        // Mocking login for testing UI
        if (email == "test@skylink.com" && password == "admin123") {
            val mockResponse = LoginResponse(
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock_token",
                email = email,
                roles = listOf("ROLE_CUSTOMER")
            )
            saveToken(mockResponse.token)
            saveEmail(email)
            return NetworkResult.Success(mockResponse)
        }

        return try {
            val response = authApi.login(LoginRequest(email, password))
            saveToken(response.token)
            saveEmail(email)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun register(email: String, password: String): NetworkResult<RegisterResponse> {
        // Mocking register for testing UI
        if (email == "test@skylink.com" && password == "admin123") {
            val mockResponse = RegisterResponse(
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock_token",
                message = "Mock Registration Successful"
            )
            saveToken(mockResponse.token)
            saveEmail(email)
            return NetworkResult.Success(mockResponse)
        }

        return try {
            val response = authApi.register(RegisterRequest(email, password, "ROLE_CUSTOMER"))
            saveToken(response.token)
            saveEmail(email)
            NetworkResult.Success(response)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): NetworkResult<RegisterResponse> {
        // Mock fallback if Firebase/Google services are not configured on the device/project yet
        if (idToken == "mock_google_token") {
            val mockResponse = RegisterResponse(
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock_google_token",
                message = "Mock Google Sign-In Successful"
            )
            saveToken(mockResponse.token)
            saveEmail("test@skylink.com")
            return NetworkResult.Success(mockResponse)
        }

        return try {
            // 1. Sign in with Firebase using the Google ID token
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            
            // Suspend until Firebase Auth completes
            val authResult = auth.signInWithCredential(credential).awaitTask()
            val email = authResult.user?.email ?: throw Exception("No email found in Google account")
            
            // 2. Try to register this user in the GDS backend.
            // If it succeeds, great. If it fails (e.g. user already exists), we try to log them in.
            val registerResult = try {
                val response = authApi.register(RegisterRequest(email, "GoogleSocialAuthPassword123!", "ROLE_CUSTOMER"))
                saveToken(response.token)
                saveEmail(email)
                NetworkResult.Success(response)
            } catch (e: Exception) {
                null
            }

            if (registerResult != null) {
                registerResult
            } else {
                // Try logging in instead
                val loginResult = authApi.login(LoginRequest(email, "GoogleSocialAuthPassword123!"))
                saveToken(loginResult.token)
                saveEmail(email)
                NetworkResult.Success(RegisterResponse(token = loginResult.token, message = "Login successful"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    private fun saveEmail(email: String) {
        sharedPreferences.edit().putString("user_email", email).apply()
    }

    override fun saveToken(token: String) {
        sharedPreferences.edit().putString("jwt_token", token).apply()
        authInterceptor.setToken(token)
    }

    override fun getToken(): String? {
        val token = sharedPreferences.getString("jwt_token", null)
        authInterceptor.setToken(token)
        return token
    }

    override fun getEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }

    override fun logout() {
        sharedPreferences.edit().remove("jwt_token").remove("user_email").apply()
        authInterceptor.setToken(null)
    }

    // Helper extension to await Firebase Tasks
    private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(task.exception ?: RuntimeException("Task failed"))
            }
        }
    }
}
