package com.alalodev.skylink.features.booking.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AirlineSeatReclineExtra
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.home.data.model.Flight
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Design Colors from Stitch
val StitchPrimary = Color(0xFF4B54A6)
val StitchBackground = Color(0xFFFCF8FF)
val StitchOnBackground = Color(0xFF1B192E)
val StitchSurface = Color(0xFFFCF8FF)
val StitchOnSurface = Color(0xFF1B192E)
val StitchSurfaceContainerLow = Color(0xFFF6F1FF)
val StitchSurfaceContainerHighest = Color(0xFFE5DFFD)
val StitchOutlineVariant = Color(0xFFC6C5D3)
val StitchPrimaryContainer = Color(0xFF646DC1)
val StitchOnPrimaryContainer = Color(0xFFFFFBFF)
val StitchTertiaryContainer = Color(0xFF697C18)
val StitchOnTertiaryContainer = Color(0xFFFCFFE2)
val StitchSecondary = Color(0xFF57633A)
val StitchTertiary = Color(0xFF526300)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingSummaryScreen(
    flightId: String,
    onNavigateBack: () -> Unit,
    onPickSeatClick: () -> Unit,
    onContinueToPaymentClick: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val flightState by viewModel.flightState.collectAsState()
    val passengerName = viewModel.passengerName

    LaunchedEffect(flightId) {
        viewModel.loadFlight(flightId)
    }

    Scaffold(
        containerColor = StitchBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = StitchPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "SkyLink",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = StitchPrimary,
                            letterSpacing = (-1).sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = StitchOnSurface
                        )
                    }
                },
                actions = {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsVQJxWEZpRrw150aFW-gAgTAvozL_khUoBKn0_TMm057Se6_0eL9OWnNu1Z-BdoMavulyX_Cr3C4tMKxdU74qBfMoJj3jM6CMbAXm6ZEWLFgPvMIXLThR0Ts-EuZiplMjUBdI6mJ3VZxHg6gk74tNrJgttwZjyw5f3VOHhHMU7Vu3razEUZ5IBfyw5U4k2-XoDus_9CzR4PB_q9I62bQ2tkOIuKEhm13hE-aXn2cuqm5bDu45eLJ8_MrZsmpd2iCH6cHzDfUnFgr5",
                        contentDescription = "Profile",
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(2.dp, StitchPrimaryContainer, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StitchBackground
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(StitchBackground, Color(0xFFF1EAFF))
                    )
                )
        ) {
            when (val state = flightState) {
                is NetworkResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = StitchPrimary)
                    }
                }
                is NetworkResult.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error al cargar los detalles del vuelo",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = state.exception.message ?: "Error desconocido")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadFlight(flightId) }) {
                            Text("Reintentar")
                        }
                    }
                }
                is NetworkResult.Success -> {
                    val flight = state.data
                    BookingSummaryContent(
                        flight = flight,
                        passengerName = passengerName,
                        onPickSeatClick = onPickSeatClick,
                        onContinueToPaymentClick = onContinueToPaymentClick
                    )
                }
            }
        }
    }
}

@Composable
fun BookingSummaryContent(
    flight: Flight,
    passengerName: String,
    onPickSeatClick: () -> Unit,
    onContinueToPaymentClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    // Parse Dates
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val departureTime = try {
        LocalDateTime.parse(flight.departureTime, formatter)
    } catch (e: Exception) {
        LocalDateTime.now()
    }
    val arrivalTime = try {
        LocalDateTime.parse(flight.arrivalTime, formatter)
    } catch (e: Exception) {
        LocalDateTime.now().plusHours(2)
    }

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())

    val durationText = "2h 15m" // Or compute it dynamically if needed

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Step Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STEP 1 OF 2",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = StitchPrimary,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Resumen",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = StitchOnBackground
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 4.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(StitchPrimary)
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 24.dp, height = 4.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(StitchOutlineVariant)
                    )
                }
            }

            // Section 1: Flight Details Card
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flight,
                        contentDescription = null,
                        tint = StitchOnSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Detalles del Vuelo",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = StitchOnSurface.copy(alpha = 0.7f)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StitchSurfaceContainerLow),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        StitchOutlineVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Top row with logos and codes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = flight.origin,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = StitchPrimary
                                )
                                Text(
                                    text = if (flight.origin == "BCN") "Barcelona" else "Madrid",
                                    fontSize = 13.sp,
                                    color = StitchOnSurface.copy(alpha = 0.7f)
                                )
                            }

                            // Route indicators
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = durationText,
                                    fontSize = 11.sp,
                                    color = StitchOutlineVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(1.dp)
                                            .background(StitchOutlineVariant)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.FlightTakeoff,
                                        contentDescription = null,
                                        tint = StitchPrimary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(horizontal = 2.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(1.dp)
                                            .background(StitchOutlineVariant)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Directo",
                                    fontSize = 11.sp,
                                    color = StitchOnSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = flight.destination,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = StitchPrimary
                                )
                                Text(
                                    text = if (flight.destination == "MAD") "Madrid" else "Barcelona",
                                    fontSize = 13.sp,
                                    color = StitchOnSurface.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Divider
                        HorizontalDivider(color = StitchOutlineVariant.copy(alpha = 0.3f))

                        // Bottom row with Dates and Times
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = departureTime.format(dateFormatter),
                                    fontSize = 12.sp,
                                    color = StitchOnSurface.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = departureTime.format(timeFormatter),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchOnSurface
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = arrivalTime.format(dateFormatter),
                                    fontSize = 12.sp,
                                    color = StitchOnSurface.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = arrivalTime.format(timeFormatter),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StitchOnSurface
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Passenger Summary
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = StitchOnSurface.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Pasajero",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = StitchOnSurface.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = "Editar",
                        color = StitchPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { /* Edit passenger */ }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StitchSurfaceContainerHighest.copy(alpha = 0.3f))
                        .border(
                            1.dp,
                            StitchOutlineVariant.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(StitchSurfaceContainerHighest, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = StitchPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = passengerName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = StitchOnSurface
                            )
                            Text(
                                text = "Adulto • Economy Classic",
                                fontSize = 14.sp,
                                color = StitchOnSurface.copy(alpha = 0.7f)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StitchPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Section 3: Enhance Your Trip
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Mejora tu viaje",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = StitchOnSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Prominent Seat Selection Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(StitchPrimaryContainer)
                        .clickable { onPickSeatClick() }
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AirlineSeatReclineExtra,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(StitchTertiaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Solo 10€ extra",
                                    color = StitchOnTertiaryContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Selecciona tu asiento",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Elige tu lugar favorito para disfrutar de las mejores vistas y mayor comodidad durante tu vuelo.",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 18.sp
                            )
                        }

                        Button(
                            onClick = onPickSeatClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = StitchPrimary
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(99.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Elegir Asiento",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Other Add-ons Bento Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Baggage card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StitchSurfaceContainerLow)
                            .border(
                                1.dp,
                                StitchOutlineVariant.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(
                                imageVector = Icons.Default.Luggage,
                                contentDescription = null,
                                tint = StitchSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Equipaje Extra",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = StitchOnSurface
                                )
                                Text(
                                    text = "Desde 25€",
                                    fontSize = 12.sp,
                                    color = StitchOnSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Food card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StitchSurfaceContainerLow)
                            .border(
                                1.dp,
                                StitchOutlineVariant.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = StitchTertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Menú Premium",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = StitchOnSurface
                                )
                                Text(
                                    text = "Desde 12€",
                                    fontSize = 12.sp,
                                    color = StitchOnSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Fixed Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(StitchBackground.copy(alpha = 0.95f))
                .border(
                    width = 1.dp,
                    color = StitchOutlineVariant.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Precio Total",
                        fontSize = 12.sp,
                        color = StitchOnSurface.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f", flight.price),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = StitchPrimary
                        )
                        Text(
                            text = "€",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StitchPrimary,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }

                Button(
                    onClick = onContinueToPaymentClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StitchPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(99.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Continuar al Pago",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
