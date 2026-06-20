package com.alalodev.skylink_kotlinapp.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alalodev.skylink_kotlinapp.data.repository.FlightRepository
import com.alalodev.skylink_kotlinapp.domain.model.Flight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    flightId: Long,
    onSeatSelected: (String, String) -> Unit,
    onBack: () -> Unit,
    flightRepository: FlightRepository
) {
    var flight by remember { mutableStateOf<Flight?>(null) }
    var selectedSeat by remember { mutableStateOf<String?>(null) }
    
    // Generar asientos mock (A-F, 1-20)
    val rows = 20
    val columns = listOf("A", "B", "C", "", "D", "E", "F") // "" es el pasillo
    
    val bookedSeats = remember { setOf("1A", "2C", "5D", "10F", "12B") }

    LaunchedEffect(flightId) {
        flight = flightRepository.getFlightById(flightId).getOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selecciona tu Asiento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = selectedSeat ?: "Ninguno",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Asiento seleccionado",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Button(
                        onClick = { selectedSeat?.let { onSeatSelected(it, "ECONOMY") } },
                        enabled = selectedSeat != null,
                        modifier = Modifier.height(56.dp).width(160.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Continuar")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem("Disponible", MaterialTheme.colorScheme.surfaceVariant)
                LegendItem("Ocupado", Color.LightGray)
                LegendItem("Tu selección", MaterialTheme.colorScheme.primary)
            }

            // Seat Map
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rows * 7) { index ->
                        val rowNum = (index / 7) + 1
                        val colIdx = index % 7
                        val colLetter = columns[colIdx]
                        
                        if (colLetter.isEmpty()) {
                            // Pasillo
                            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                Text(rowNum.toString(), fontSize = 10.sp, color = Color.Gray)
                            }
                        } else {
                            val seatId = "$rowNum$colLetter"
                            val isBooked = bookedSeats.contains(seatId)
                            val isSelected = selectedSeat == seatId
                            
                            SeatIcon(
                                seatId = seatId,
                                isBooked = isBooked,
                                isSelected = isSelected,
                                onClick = { if (!isBooked) selectedSeat = seatId }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SeatIcon(
    seatId: String,
    isBooked: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isBooked -> Color.LightGray
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when {
        isSelected -> Color.White
        isBooked -> Color.DarkGray
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(enabled = !isBooked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = seatId.takeLast(1),
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
