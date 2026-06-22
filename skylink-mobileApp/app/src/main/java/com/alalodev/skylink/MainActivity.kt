package com.alalodev.skylink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alalodev.skylink.features.auth.presentation.AuthViewModel
import com.alalodev.skylink.features.auth.presentation.LoginScreen
import com.alalodev.skylink.features.auth.presentation.RegisterScreen
import com.alalodev.skylink.features.home.presentation.HomeScreen
import com.alalodev.skylink.ui.theme.SkyLinkTheme
import dagger.hilt.android.AndroidEntryPoint
import com.alalodev.skylink.features.home.presentation.HomeViewModel
import com.alalodev.skylink.features.home.presentation.FlightResultsScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.alalodev.skylink.features.booking.presentation.BookingSummaryScreen
import com.alalodev.skylink.features.booking.presentation.SeatSelectionScreen
import com.alalodev.skylink.features.booking.presentation.CheckoutPaymentScreen
import com.alalodev.skylink.features.booking.presentation.MyBookingsScreen
import com.alalodev.skylink.features.booking.presentation.BoardingPassScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val locale = java.util.Locale("es", "ES")
        java.util.Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkyLinkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            val viewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // Navigate to Home page and remove auth screens from stack
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onSearchClick = { origin, destination ->
                    navController.navigate("flight_results/$origin/$destination")
                },
                onNavigateToMyBookings = {
                    navController.navigate("my_bookings")
                }
            )
        }
        composable(
            route = "flight_results/{origin}/{destination}",
            arguments = listOf(
                navArgument("origin") { type = NavType.StringType },
                navArgument("destination") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val origin = backStackEntry.arguments?.getString("origin") ?: "MAD"
            val destination = backStackEntry.arguments?.getString("destination") ?: "BCN"
            val homeViewModel: HomeViewModel = hiltViewModel()
            FlightResultsScreen(
                originCode = origin,
                destinationCode = destination,
                onNavigateBack = { navController.popBackStack() },
                onFlightSelected = { flightId ->
                    navController.navigate("booking_summary/$flightId")
                },
                viewModel = homeViewModel
            )
        }
        composable(
            route = "booking_summary/{flightId}",
            arguments = listOf(
                navArgument("flightId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val flightId = backStackEntry.arguments?.getString("flightId") ?: ""
            BookingSummaryScreen(
                flightId = flightId,
                onNavigateBack = { navController.popBackStack() },
                onPickSeatClick = {
                    navController.navigate("seat_selection/$flightId")
                },
                onContinueToPaymentClick = {
                    navController.navigate("checkout_payment/$flightId/none")
                }
            )
        }
        composable(
            route = "seat_selection/{flightId}",
            arguments = listOf(
                navArgument("flightId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val flightId = backStackEntry.arguments?.getString("flightId") ?: ""
            SeatSelectionScreen(
                flightId = flightId,
                onNavigateBack = { navController.popBackStack() },
                onSeatSelected = { seatNumber ->
                    navController.navigate("checkout_payment/$flightId/$seatNumber") {
                        // Pop up to booking_summary to avoid returning to seat selection from checkout
                        popUpTo("booking_summary/$flightId")
                    }
                }
            )
        }
        composable(
            route = "checkout_payment/{flightId}/{seatNumber}",
            arguments = listOf(
                navArgument("flightId") { type = NavType.StringType },
                navArgument("seatNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val flightId = backStackEntry.arguments?.getString("flightId") ?: ""
            val seatNumber = backStackEntry.arguments?.getString("seatNumber") ?: "none"
            CheckoutPaymentScreen(
                flightId = flightId,
                seatNumber = seatNumber,
                onNavigateBack = { navController.popBackStack() },
                onPaymentSuccess = {
                    // Navegar a Mis Viajes limpiando el stack hasta Home
                    navController.navigate("my_bookings") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }
        composable("my_bookings") {
            MyBookingsScreen(
                onNavigateToSearch = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToBoardingPass = { bookingId ->
                    navController.navigate("boarding_pass/$bookingId")
                }
            )
        }
        composable(
            route = "boarding_pass/{bookingId}",
            arguments = listOf(
                navArgument("bookingId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getLong("bookingId") ?: 0L
            BoardingPassScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
