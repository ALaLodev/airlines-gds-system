package com.alalodev.skylink_kotlinapp.ui.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alalodev.skylink_kotlinapp.data.repository.FlightRepository
import com.alalodev.skylink_kotlinapp.domain.model.Flight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmationScreen(
    flightId: Long,
    onBookingSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel(),
    flightRepository: FlightRepository // Lo pasamos directamente o vía un FlightViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var flight by remember { mutableStateOf<Flight?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(flightId) {
        flight = flightRepository.getFlightById(flightId).getOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Reserva") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            flight?.let { f ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(text = "Resumen del Vuelo", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "${f.origin} -> ${f.destination}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(text = "$${f.price}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(text = "Vuelo: ${f.flightNumber}", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Al confirmar, se iniciará el proceso de pago y reserva de asiento.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { 
                        // Mock user ID 1 for now
                        viewModel.createReservation(f, "12A", "ECONOMY", 1L) 
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = uiState !is BookingUiState.Loading && uiState !is BookingUiState.Success
                ) {
                    if (uiState is BookingUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Confirmar y Reservar")
                    }
                }
            }

            if (uiState is BookingUiState.Success) {
                AlertDialog(
                    onDismissRequest = onBookingSuccess,
                    confirmButton = {
                        Button(onClick = onBookingSuccess) { Text("Entendido") }
                    },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF6C7F1B), modifier = Modifier.size(48.dp)) },
                    title = { Text("¡Reserva Creada!") },
                    text = { Text("Tu reserva con PNR ${(uiState as BookingUiState.Success).reservation.pnr} ha sido procesada. Recibirás un correo de confirmación.") }
                )
            }
        }
    }
}
