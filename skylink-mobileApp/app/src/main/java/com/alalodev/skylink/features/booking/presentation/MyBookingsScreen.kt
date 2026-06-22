package com.alalodev.skylink.features.booking.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToBoardingPass: (Long) -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val bookingsState by viewModel.userBookingsState.collectAsState()
    val passengerName = viewModel.passengerName
    var tabSelected by remember { mutableStateOf(0) } // 0 = Próximos, 1 = Pasados

    LaunchedEffect(Unit) {
        viewModel.loadUserBookings()
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
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .background(StitchPrimaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = passengerName.split(" ").filter { it.isNotEmpty() }.map { it.first().uppercase() }.take(2).joinToString("")
                        Text(
                            text = if (initials.isNotEmpty()) initials else "JD",
                            color = StitchOnPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StitchBackground)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSearch,
                    icon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    label = { Text("Buscar") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Ya estamos aquí */ },
                    icon = { Icon(Icons.Default.TravelExplore, contentDescription = "Mis Viajes") },
                    label = { Text("Mis Viajes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StitchPrimary,
                        selectedTextColor = StitchPrimary,
                        indicatorColor = StitchSurfaceContainerHighest
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Alertas") },
                    label = { Text("Alertas") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Mis Viajes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = StitchOnBackground,
                    letterSpacing = (-0.5).sp
                )

                // Tabs Selector
                TabRow(
                    selectedTabIndex = tabSelected,
                    containerColor = Color.Transparent,
                    contentColor = StitchPrimary,
                    divider = { HorizontalDivider(color = StitchOutlineVariant.copy(alpha = 0.3f)) }
                ) {
                    Tab(
                        selected = tabSelected == 0,
                        onClick = { tabSelected = 0 },
                        text = {
                            Text(
                                "Próximos",
                                fontWeight = if (tabSelected == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = tabSelected == 1,
                        onClick = { tabSelected = 1 },
                        text = {
                            Text(
                                "Pasados",
                                fontWeight = if (tabSelected == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    )
                }

                // Bookings content
                when (val state = bookingsState) {
                    is NetworkResult.Loading -> {
                        Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = StitchPrimary)
                        }
                    }
                    is NetworkResult.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize().weight(1f).padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error al cargar tus reservas",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = state.exception.message ?: "Error desconocido")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadUserBookings() },
                                colors = ButtonDefaults.buttonColors(containerColor = StitchPrimary)
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                    is NetworkResult.Success -> {
                        val bookings = state.data
                        val now = LocalDateTime.now()

                        val upcomingBookings = bookings.filter { booking ->
                            val departureStr = booking.flight?.departureTime
                            val departure = try {
                                LocalDateTime.parse(departureStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            } catch (e: Exception) {
                                null
                            }
                            departure == null || departure.isAfter(now)
                        }

                        val pastBookings = bookings.filter { booking ->
                            val departureStr = booking.flight?.departureTime
                            val departure = try {
                                LocalDateTime.parse(departureStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            } catch (e: Exception) {
                                null
                            }
                            departure != null && departure.isBefore(now)
                        }

                        val activeList = if (tabSelected == 0) upcomingBookings else pastBookings

                        if (activeList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize().weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FlightTakeoff,
                                        contentDescription = null,
                                        tint = StitchOutlineVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = if (tabSelected == 0) "No tienes viajes próximos" else "No tienes viajes pasados",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp,
                                        color = StitchOnSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(activeList) { booking ->
                                    BookingCard(
                                        booking = booking,
                                        onClick = { onNavigateToBoardingPass(booking.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: UserBooking, onClick: () -> Unit) {
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

    // Dynamic destination image based on airport code
    val imageUrl = when (flight?.destination) {
        "MAD" -> "https://lh3.googleusercontent.com/aida-public/AB6AXuBRIFcKKL3aPjPkiOE-tO_sb8iPyCnkXytpKLoL3PVmE-Ho3u9STBaa9bKTX5KJ5d3ufrNA5Al4CzR2G2fSDC2rDCezwWnB_3XMfBp4rm6APig-MUDmzft0PuW_c2L7UC2V2r5Zbg2-YsL513kL0cPCxfR0_374Z_11q_zplcgI_QdWql7dSrSTXnptYGVjVVFTVu51heuKYMuDTUw3tpC7VBC9h9E2MfN10X88YUV_sAlEdrIkL2puWGZtxTdIPTdOmPslnRcPAEEG" // Rome style/Madrid placeholder
        "BCN" -> "https://lh3.googleusercontent.com/aida-public/AB6AXuCTnzRyCTzNxEYnjac9bDYW5kEvxNgTl89V2HNnrxd_zeI9ulHZuZA2AbRwTEXMQ5lYtKnPjvlNSeaw66IE9_hsvwispaB09mj4DxjIhUQYxiM4fUt5lbbK6B-sTVaUkFNoowHwbIGseVWTBXqfr2DWuy0_-aqWsfDJDe56zX1fgv3pTRqoVxUafp2eq81E5LIsnI5Jz-v9q2MAmZv3TWANirKwRtpcZpOG7E2klvG9mKzJS1PMT7sglsf6nsOXuWprNNt56vFL50_w" // Alps/Barcelona placeholder
        else -> "https://lh3.googleusercontent.com/aida-public/AB6AXuDhM-pJ_i9j8R-AcrHYO_Swk8PC92v_CplAMboYVxgHt85jEZowJmL6p-R4X-xU2USkMiDDHMFzRbWlnAqKx_9RAzjr342j-9ULHpV0biFhWX7Tu7PwQDXV4n5DkQs_xtGXXJ_67-xmrfsERlX-w4jjTI8RBPCNmWHsl78_fAGjDm5MkjRYLjPtQPDFEhcHzD6sMhXgRtBRnsZJkjvdClXDMTqtw4-xesY9X1lHEynAoiI7h7oI2ou-DA1OtZ-JlC2JevR49crJ45jx"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                1.dp,
                StitchOutlineVariant.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color(0xFFE8F5E9)) // Light green
                            .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(99.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Confirmado",
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = dateStr,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = StitchOnSurface.copy(alpha = 0.6f)
                    )
                }

                if (booking.seatNumber != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(StitchPrimary.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Asiento ${booking.seatNumber} • ${if (booking.cabinClass == "BUSINESS") "Business" else "Turista"}",
                            color = StitchPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Route details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = flight?.origin ?: "BCN",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = StitchPrimary
                    )
                    Text(
                        text = if (flight?.origin == "BCN") "Barcelona" else "Madrid",
                        fontSize = 13.sp,
                        color = StitchOnSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = departureTimeStr,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Flight Icon and dashed line
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flight,
                        contentDescription = null,
                        tint = StitchSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(StitchOutlineVariant.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2h 15m",
                        fontSize = 11.sp,
                        color = StitchOnSurface.copy(alpha = 0.5f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = flight?.destination ?: "MAD",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = StitchPrimary
                    )
                    Text(
                        text = if (flight?.destination == "MAD") "Madrid" else "Barcelona",
                        fontSize = 13.sp,
                        color = StitchOnSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = arrivalTimeStr,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Divider
            HorizontalDivider(color = StitchOutlineVariant.copy(alpha = 0.2f))

            // Booking PNR and destination image
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "CÓDIGO DE RESERVA (PNR)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchOutlineVariant,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = booking.pnr,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = StitchOnSurface,
                        letterSpacing = 1.5.sp
                    )
                }

                // Destination Image Thumbnail
                Card(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Destino",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
