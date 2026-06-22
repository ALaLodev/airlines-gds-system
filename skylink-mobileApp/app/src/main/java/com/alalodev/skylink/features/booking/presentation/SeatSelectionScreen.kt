package com.alalodev.skylink.features.booking.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AirlineSeatLegroomExtra
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.home.data.model.Flight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    flightId: String,
    onNavigateBack: () -> Unit,
    onSeatSelected: (String) -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val flightState by viewModel.flightState.collectAsState()
    val bookedSeats by viewModel.bookedSeatsState.collectAsState()
    var selectedSeat by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(flightId) {
        viewModel.loadFlight(flightId)
        viewModel.loadBookedSeats(flightId)
    }

    Scaffold(
        containerColor = StitchBackground,
        topBar = {
            TopAppBar(
                title = { Text("Select Seat", fontWeight = FontWeight.Bold, color = StitchPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = StitchPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StitchBackground)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(StitchBackground)
        ) {
            when (val state = flightState) {
                is NetworkResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = StitchPrimary)
                    }
                }
                is NetworkResult.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error loading seat map", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { 
                            viewModel.loadFlight(flightId)
                            viewModel.loadBookedSeats(flightId)
                        }) { Text("Retry") }
                    }
                }
                is NetworkResult.Success -> {
                    val flight = state.data
                    SeatSelectionContent(
                        flight = flight,
                        bookedSeats = bookedSeats,
                        selectedSeat = selectedSeat,
                        onSeatTap = { selectedSeat = it },
                        onConfirm = { selectedSeat?.let { onSeatSelected(it) } }
                    )
                }
            }
        }
    }
}

@Composable
fun SeatSelectionContent(
    flight: Flight,
    bookedSeats: List<String>,
    selectedSeat: String?,
    onSeatTap: (String) -> Unit,
    onConfirm: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Flight Context Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .border(width = 1.dp, color = StitchOutlineVariant.copy(alpha = 0.2f)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = flight.origin, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = StitchOutlineVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = flight.destination, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .size(width = 1.dp, height = 16.dp)
                        .background(StitchOutlineVariant)
                )
                Text(
                    text = "Airbus A350",
                    fontSize = 13.sp,
                    color = StitchOnSurface.copy(alpha = 0.7f)
                )
            }

            // Legend Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StitchBackground)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Available
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(1.dp, StitchOutlineVariant, RoundedCornerShape(4.dp))
                            .background(Color.White, RoundedCornerShape(4.dp))
                    )
                    Text(text = "Libre", fontSize = 12.sp, color = StitchOnSurface.copy(alpha = 0.7f))
                }
                // Occupied
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(StitchOutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = StitchOutlineVariant,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    Text(text = "Ocupado", fontSize = 12.sp, color = StitchOnSurface.copy(alpha = 0.7f))
                }
                // Selected
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(StitchPrimaryContainer, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(text = "Seleccionado", fontSize = 12.sp, color = StitchPrimary)
                }
            }

            // Scrollable Seat Map
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                // Plane cabin background shape
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(horizontal = 24.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    StitchSurfaceContainerLow,
                                    Color.White
                                )
                            ),
                            shape = RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 80.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = StitchOutlineVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 80.dp)
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Business Class Section
                    Text(
                        text = "CLASE BUSINESS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = StitchOnSurface.copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Business Rows (Rows 1 to 3)
                    for (row in 1..3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Seats (A, C)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                BusinessSeat(row = row, col = "A", bookedSeats = bookedSeats, selectedSeat = selectedSeat, onSeatTap = onSeatTap)
                                BusinessSeat(row = row, col = "C", bookedSeats = bookedSeats, selectedSeat = selectedSeat, onSeatTap = onSeatTap)
                            }

                            // Aisle
                            Text(
                                text = row.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = StitchOnSurface.copy(alpha = 0.3f),
                                modifier = Modifier.width(20.dp)
                            )

                            // Right Seats (H, K)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                BusinessSeat(row = row, col = "H", bookedSeats = bookedSeats, selectedSeat = selectedSeat, onSeatTap = onSeatTap)
                                BusinessSeat(row = row, col = "K", bookedSeats = bookedSeats, selectedSeat = selectedSeat, onSeatTap = onSeatTap)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = StitchOutlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Economy Section
                    Text(
                        text = "CLASE TURISTA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = StitchOnSurface.copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Economy Rows (Rows 12 to 20)
                    for (row in 12..20) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Seats (A, B, C)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                EconomySeat(row = row, col = "A", bookedSeats = bookedSeats, selectedSeat = selectedSeat, onSeatTap = onSeatTap)
                                EconomySeat(row = row, col = "B", bookedSeats = bookedSeats, selectedSeat = selectedSeat, onSeatTap = onSeatTap)
                                EconomySeat(row = row, col = "C", bookedSeats = bookedSeats, selectedSeat = selectedSeat, onSeatTap = onSeatTap)
                            }

                            // Aisle
                            Text(
                                text = row.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = StitchOnSurface.copy(alpha = 0.3f),
                                modifier = Modifier.width(20.dp)
                            )

                            // Right Seats (D, E, F)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                EconomySeat(row = row, col = "D", bookedSeats = bookedSeats, selectedSeat = selectedSeat, onSeatTap = onSeatTap)
                                EconomySeat(row = row, col = "E", bookedSeats = bookedSeats, selectedSeat = selectedSeat, onSeatTap = onSeatTap)
                                EconomySeat(row = row, col = "F", bookedSeats = bookedSeats, selectedSeat = selectedSeat, onSeatTap = onSeatTap)
                            }
                        }
                    }
                }
            }
        }

        // Bottom Sheet Selection Summary
        AnimatedVisibility(
            visible = selectedSeat != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            if (selectedSeat != null) {
                val row = selectedSeat.filter { it.isDigit() }.toIntOrNull() ?: 12
                val isBusiness = row in 1..3
                val seatPrice = if (isBusiness) 23 else 3

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            StitchOutlineVariant.copy(alpha = 0.2f),
                            RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        ),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Drag Indicator
                        Box(
                            modifier = Modifier
                                .size(width = 48.dp, height = 4.dp)
                                .clip(RoundedCornerShape(99.dp))
                                .background(StitchOutlineVariant.copy(alpha = 0.5f))
                                .align(Alignment.CenterHorizontally)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Asiento $selectedSeat",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchOnSurface
                                )
                                Text(
                                    text = if (isBusiness) "Business • Premium Flatbed" else "Turista • Mayor Espacio",
                                    fontSize = 14.sp,
                                    color = StitchOnSurface.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                text = "${seatPrice}€",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = StitchPrimary
                            )
                        }

                        // Chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(StitchSecondary.copy(alpha = 0.1f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AirlineSeatLegroomExtra,
                                    contentDescription = null,
                                    tint = StitchSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Más Espacio",
                                    fontSize = 12.sp,
                                    color = StitchSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(StitchOutlineVariant.copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Power,
                                    contentDescription = null,
                                    tint = StitchOnSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Tomacorriente",
                                    fontSize = 12.sp,
                                    color = StitchOnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StitchPrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(99.dp)
                        ) {
                            Text(
                                text = "Confirmar Selección",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessSeat(
    row: Int,
    col: String,
    bookedSeats: List<String>,
    selectedSeat: String?,
    onSeatTap: (String) -> Unit
) {
    val seatId = "$row$col"
    val isSelected = selectedSeat == seatId
    val isOccupied = bookedSeats.contains(seatId)

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 48.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
            .background(
                when {
                    isOccupied -> StitchOutlineVariant.copy(alpha = 0.3f)
                    isSelected -> StitchPrimaryContainer
                    else -> Color.White
                }
            )
            .border(
                width = 1.dp,
                color = when {
                    isOccupied -> Color.Transparent
                    isSelected -> Color.Transparent
                    else -> StitchSecondary
                },
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
            )
            .clickable(enabled = !isOccupied) { onSeatTap(seatId) },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        } else if (isOccupied) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = StitchOutlineVariant,
                modifier = Modifier.size(16.dp)
            )
        } else {
            // Little headrest bar
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.7f)
                    .height(4.dp)
                    .background(StitchSecondary.copy(alpha = 0.3f), RoundedCornerShape(99.dp))
                    .padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun EconomySeat(
    row: Int,
    col: String,
    bookedSeats: List<String>,
    selectedSeat: String?,
    onSeatTap: (String) -> Unit
) {
    val seatId = "$row$col"
    val isSelected = selectedSeat == seatId
    val isOccupied = bookedSeats.contains(seatId)

    Box(
        modifier = Modifier
            .size(width = 36.dp, height = 40.dp)
            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 3.dp, bottomEnd = 3.dp))
            .background(
                when {
                    isOccupied -> StitchOutlineVariant.copy(alpha = 0.3f)
                    isSelected -> StitchPrimaryContainer
                    else -> StitchSurfaceContainerLow
                }
            )
            .border(
                width = 1.dp,
                color = when {
                    isOccupied -> Color.Transparent
                    isSelected -> Color.Transparent
                    else -> StitchOutlineVariant
                },
                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 3.dp, bottomEnd = 3.dp)
            )
            .clickable(enabled = !isOccupied) { onSeatTap(seatId) },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        } else if (isOccupied) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = StitchOutlineVariant,
                modifier = Modifier.size(14.dp)
            )
        } else {
            // Little headrest bar
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.7f)
                    .height(3.dp)
                    .background(StitchOutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(99.dp))
                    .padding(top = 1.dp)
            )
        }
    }
}
