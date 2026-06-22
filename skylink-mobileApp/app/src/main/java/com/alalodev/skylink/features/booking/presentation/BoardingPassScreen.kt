package com.alalodev.skylink.features.booking.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alalodev.skylink.core.network.util.NetworkResult
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardingPassScreen(
    bookingId: Long,
    onNavigateBack: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val bookingsState by viewModel.userBookingsState.collectAsState()
    val passengerName = viewModel.passengerName

    LaunchedEffect(Unit) {
        viewModel.loadUserBookings()
    }

    val booking = (bookingsState as? NetworkResult.Success)?.data?.find { it.id == bookingId }

    Scaffold(
        containerColor = StitchBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tarjeta de Embarque",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = StitchPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = StitchOnSurface
                        )
                    }
                },
                actions = {
                    // Empty box to balance the navigation icon and center the title
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StitchBackground)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(StitchBackground, Color(0xFFF1EAFF))
                    )
                )
        ) {
            if (booking == null) {
                if (bookingsState is NetworkResult.Loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = StitchPrimary)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No se encontró la reserva",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = StitchPrimary)
                        ) {
                            Text("Volver")
                        }
                    }
                }
            } else {
                val flight = booking.flight
                val dateStr = if (flight != null) {
                    try {
                        val departure = LocalDateTime.parse(flight.departureTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        departure.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES")))
                    } catch (e: Exception) {
                        "24 Oct 2026"
                    }
                } else {
                    "24 Oct 2026"
                }

                val departureTimeStr = if (flight != null) {
                    try {
                        val departure = LocalDateTime.parse(flight.departureTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        departure.format(DateTimeFormatter.ofPattern("HH:mm"))
                    } catch (e: Exception) {
                        "12:00"
                    }
                } else {
                    "12:00"
                }

                val arrivalTimeStr = if (flight != null) {
                    try {
                        val arrival = LocalDateTime.parse(flight.arrivalTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        arrival.format(DateTimeFormatter.ofPattern("HH:mm"))
                    } catch (e: Exception) {
                        "14:15"
                    }
                } else {
                    "14:15"
                }

                val durationStr = "2h 15m" // Standard flight duration between BCN/MAD

                // Terminal, Gate and Seat mappings (consistent pseudo-random mapping based on booking ID)
                val gateIndex = (booking.id % 5).toInt()
                val gateLetter = listOf("A", "B", "C")[gateIndex % 3]
                val gateNum = (booking.id * 7 % 29) + 1
                val gateStr = "$gateLetter$gateNum"
                val terminalStr = if (flight?.origin == "BCN") "1" else "4"

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ticket Cutout Box Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, StitchOutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Top header: SkyLink
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(StitchPrimary)
                                    .padding(24.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FlightTakeoff,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = "SkyLink",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 20.sp,
                                            letterSpacing = (-1).sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(99.dp))
                                            .background(Color.White.copy(alpha = 0.15f))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (booking.cabinClass == "BUSINESS") "Business" else "Turista",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Route details BCN -> MAD
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = flight?.origin ?: "BCN",
                                            fontSize = 36.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (flight?.origin == "BCN") "Barcelona" else "Madrid",
                                            fontSize = 13.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }

                                    // Mid flight arrow
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(80.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flight,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(Color.White.copy(alpha = 0.4f))
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = durationStr,
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = flight?.destination ?: "MAD",
                                            fontSize = 36.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (flight?.destination == "MAD") "Madrid" else "Barcelona",
                                            fontSize = 13.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }

                            // Middle section: Flight Details
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .padding(top = 28.dp, bottom = 28.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "FECHA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchOutlineVariant,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = dateStr,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = StitchOnSurface
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = "PASAJERO",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchOutlineVariant,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = passengerName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = StitchOnSurface,
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "SALIDA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchOutlineVariant,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = departureTimeStr,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = StitchOnSurface
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = "LLEGADA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchOutlineVariant,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = arrivalTimeStr,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = StitchOnSurface
                                        )
                                    }
                                }
                            }

                            // Empty space where cutouts are placed, with a dashed separator
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .height(2.dp)
                                ) {
                                    drawLine(
                                        color = StitchOutlineVariant.copy(alpha = 0.5f),
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                            }

                            // Bottom Section: Terminal & Gate & Seat & QR Code
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .padding(top = 16.dp, bottom = 28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StitchSurfaceContainerLow)
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "TERMINAL",
                                            fontSize = 10.sp,
                                            color = StitchOnSurface.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = terminalStr,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchPrimary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(28.dp)
                                            .background(StitchOutlineVariant.copy(alpha = 0.3f))
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "PUERTA",
                                            fontSize = 10.sp,
                                            color = StitchOnSurface.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = gateStr,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchPrimary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(28.dp)
                                            .background(StitchOutlineVariant.copy(alpha = 0.3f))
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "ASIENTO",
                                            fontSize = 10.sp,
                                            color = StitchOnSurface.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = booking.seatNumber ?: "A1",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StitchPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(28.dp))

                                // QR Code Display Box
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .border(1.dp, StitchOutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                        .padding(16.dp)
                                ) {
                                    val qrData = "SL-${booking.id}-${booking.pnr}-${booking.seatNumber ?: "none"}"
                                    BoardingPassQrCode(
                                        text = qrData,
                                        modifier = Modifier.size(176.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "SL-${booking.id}-${booking.pnr.uppercase()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchOutlineVariant,
                                    letterSpacing = 2.sp
                                )
                            }
                        }

                        // Left cutout circle
                        Box(
                            modifier = Modifier
                                .offset(x = (-12).dp)
                                .size(24.dp)
                                .align(Alignment.CenterStart)
                                .clip(CircleShape)
                                .background(StitchBackground)
                        )

                        // Right cutout circle
                        Box(
                            modifier = Modifier
                                .offset(x = 12.dp)
                                .size(24.dp)
                                .align(Alignment.CenterEnd)
                                .clip(CircleShape)
                                .background(StitchBackground)
                        )
                    }

                    // Bottom Action buttons
                    Button(
                        onClick = { /* Simulated Wallet Action */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StitchOnBackground),
                        shape = RoundedCornerShape(27.dp),
                        elevation = ButtonDefaults.buttonElevation()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wallet,
                                contentDescription = null,
                                tint = StitchBackground
                            )
                            Text(
                                text = "Añadir a Google Wallet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = StitchBackground
                            )
                        }
                    }

                    Button(
                        onClick = { /* Simulated PDF download */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .border(1.dp, StitchPrimary.copy(alpha = 0.15f), RoundedCornerShape(27.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = StitchSurfaceContainerLow),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = StitchPrimary
                            )
                            Text(
                                text = "Descargar PDF",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = StitchPrimary
                            )
                        }
                    }

                    // Contextual Boarding Pass Footer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "El embarque cierra 20 minutos antes de la salida",
                            fontSize = 13.sp,
                            color = StitchOnSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Estado del Vuelo",
                                color = StitchPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = "•", color = StitchOutlineVariant)
                            Text(
                                text = "Mapa Terminal",
                                color = StitchPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = "•", color = StitchOutlineVariant)
                            Text(
                                text = "Ayuda",
                                color = StitchPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoardingPassQrCode(text: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val size = size.width
        val cellSize = size / 21f // 21x21 QR code grid

        // Draw background
        drawRect(Color.White)

        // Draw Finder Patterns (three corners: top-left, top-right, bottom-left)
        drawFinderPattern(0f, 0f, cellSize)
        drawFinderPattern((21 - 7) * cellSize, 0f, cellSize)
        drawFinderPattern(0f, (21 - 7) * cellSize, cellSize)

        // Draw alignment pattern at bottom-right area (around 14, 14)
        drawAlignmentPattern((21 - 9) * cellSize, (21 - 9) * cellSize, cellSize)

        // Fill in random QR-like noise (pseudo-randomly based on string hash to remain stable)
        val seed = text.hashCode()
        val random = java.util.Random(seed.toLong())
        for (row in 0 until 21) {
            for (col in 0 until 21) {
                // Skip finder patterns areas
                if (row < 8 && col < 8) continue // top-left
                if (row < 8 && col >= 13) continue // top-right
                if (row >= 13 && col < 8) continue // bottom-left
                // Skip alignment pattern area
                if (row in 11..13 && col in 11..13) continue

                // Pseudo-random dots
                if (random.nextBoolean()) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawFinderPattern(
    x: Float,
    y: Float,
    cellSize: Float
) {
    // 7x7 outer square
    drawRect(
        color = Color.Black,
        topLeft = Offset(x, y),
        size = Size(cellSize * 7, cellSize * 7)
    )
    // 5x5 inner white square
    drawRect(
        color = Color.White,
        topLeft = Offset(x + cellSize, y + cellSize),
        size = Size(cellSize * 5, cellSize * 5)
    )
    // 3x3 center black square
    drawRect(
        color = Color.Black,
        topLeft = Offset(x + cellSize * 2, y + cellSize * 2),
        size = Size(cellSize * 3, cellSize * 3)
    )
}

private fun DrawScope.drawAlignmentPattern(
    x: Float,
    y: Float,
    cellSize: Float
) {
    // 5x5 outer square
    drawRect(
        color = Color.Black,
        topLeft = Offset(x, y),
        size = Size(cellSize * 5, cellSize * 5)
    )
    // 3x3 inner white square
    drawRect(
        color = Color.White,
        topLeft = Offset(x + cellSize, y + cellSize),
        size = Size(cellSize * 3, cellSize * 3)
    )
    // 1x1 center black square
    drawRect(
        color = Color.Black,
        topLeft = Offset(x + cellSize * 2, y + cellSize * 2),
        size = Size(cellSize, cellSize)
    )
}
