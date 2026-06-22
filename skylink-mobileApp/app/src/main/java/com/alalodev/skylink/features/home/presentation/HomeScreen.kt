package com.alalodev.skylink.features.home.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.AirplaneTicket
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onSearchClick: (String, String) -> Unit = { _, _ -> },
    onNavigateToMyBookings: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Estados del ViewModel
    val tripType by viewModel.tripType.collectAsState()
    val origin by viewModel.origin.collectAsState()
    val destination by viewModel.destination.collectAsState()
    val departureDate by viewModel.departureDate.collectAsState()
    val returnDate by viewModel.returnDate.collectAsState()
    val adultsCount by viewModel.adultsCount.collectAsState()
    val childrenCount by viewModel.childrenCount.collectAsState()

    // Estados de diálogo
    val showOriginSelector by viewModel.showOriginSelector.collectAsState()
    val showDestinationSelector by viewModel.showDestinationSelector.collectAsState()
    val showTravelerSelector by viewModel.showTravelerSelector.collectAsState()
    val showDatePicker by viewModel.showDatePicker.collectAsState()

    // Estado local para navegación inferior
    var selectedNavItem by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SkyLink",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Perfil de usuario", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedNavItem == 0,
                    onClick = { selectedNavItem = 0 },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Buscar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
                NavigationBarItem(
                    selected = selectedNavItem == 1,
                    onClick = {
                        selectedNavItem = 1
                        onNavigateToMyBookings()
                    },
                    icon = { Icon(Icons.Default.TravelExplore, contentDescription = "My Trips") },
                    label = { Text("Mis Viajes") }
                )
                NavigationBarItem(
                    selected = selectedNavItem == 2,
                    onClick = {
                        selectedNavItem = 2
                        Toast.makeText(context, "Alertas y Notificaciones", Toast.LENGTH_SHORT).show()
                    },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
                    label = { Text("Alertas") }
                )
                NavigationBarItem(
                    selected = selectedNavItem == 3,
                    onClick = {
                        selectedNavItem = 3
                        Toast.makeText(context, "Configuración de Perfil", Toast.LENGTH_SHORT).show()
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Hero section with Search Widget
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(24.dp))
            ) {
                // Background Image via Coil
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuD2eH4OlFbrJUhwqllVnBFhcWNs8j0OEyZv6KPsfPw0lj3Md9ZFkyk3W1kocBuZ2pwQR6RsG5E0eIApvkyTA2SlhfKxJWGDOZOoQl1nXlBSAgxE3eFDyoBcqmUYG4IS97ephuts-P9MwLA9gILZb8eq3Bh5g43rsg1M8PHiyAYeKw65aCfgWE0t3LoQH3o44hjk16YYBzTNPe60aWK_UOBIKkI9M4q5D6Oi3WSGSI0svf-BZErMay2ORIhfZ-Lu0J5vMBvqz1Ak3to3",
                    contentDescription = "Beautiful Beach Background",
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                )

                // Search Widget Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                    Text(
                        text = "¿A dónde viajas hoy?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )

                    BentoSearchCard(
                        viewModel = viewModel,
                        onSearchClick = onSearchClick
                    )
                }
            }

            // Quick Links Grid Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Servicios Rápidos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                QuickLinksGrid(
                    onManageTripClick = onNavigateToMyBookings,
                    onCardClick = { title ->
                        Toast.makeText(context, "$title - Próximamente", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Explore Destinations Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explorar Destinos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = {
                        Toast.makeText(context, "Mostrando todos los destinos", Toast.LENGTH_SHORT).show()
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Ver todo",
                                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                ExploreDestinationsCarousel(
                    onPromoClick = { destinationName ->
                        // Al hacer clic, buscamos el aeropuerto correspondiente en la lista
                        val selectedAirport = viewModel.airports.find { it.name.contains(destinationName, ignoreCase = true) }
                        if (selectedAirport != null) {
                            viewModel.setDestination(selectedAirport)
                            // Navegar directamente a la búsqueda de vuelos desde el origen actual
                            onSearchClick(origin.code, selectedAirport.code)
                        } else {
                            Toast.makeText(context, "Buscando vuelos a $destinationName...", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // --- DIÁLOGOS DE SELECCIÓN ---

    // 1. Selector de Origen
    if (showOriginSelector) {
        AirportSelectorDialog(
            title = "Seleccionar Origen",
            airports = viewModel.airports,
            onDismiss = { viewModel.toggleOriginSelector(false) },
            onSelect = { viewModel.setOrigin(it) }
        )
    }

    // 2. Selector de Destino
    if (showDestinationSelector) {
        AirportSelectorDialog(
            title = "Seleccionar Destino",
            airports = viewModel.airports,
            onDismiss = { viewModel.toggleDestinationSelector(false) },
            onSelect = { viewModel.setDestination(it) }
        )
    }

    // 3. Selector de Viajeros
    if (showTravelerSelector) {
        TravelerSelectorDialog(
            adultsCount = adultsCount,
            childrenCount = childrenCount,
            onIncrementAdults = { viewModel.incrementAdults() },
            onDecrementAdults = { viewModel.decrementAdults() },
            onIncrementChildren = { viewModel.incrementChildren() },
            onDecrementChildren = { viewModel.decrementChildren() },
            onDismiss = { viewModel.toggleTravelerSelector(false) }
        )
    }

    // 4. Selector de Fecha
    if (showDatePicker) {
        if (tripType == 0) {
            DateRangePickerDialogComponent(
                initialStartDate = departureDate,
                initialEndDate = returnDate,
                onDatesSelected = { startDate, endDate ->
                    viewModel.setDates(startDate, endDate)
                },
                onDismiss = { viewModel.toggleDatePicker(false) }
            )
        } else {
            DatePickerDialogComponent(
                initialDate = departureDate,
                onDateSelected = { viewModel.setDepartureDate(it) },
                onDismiss = { viewModel.toggleDatePicker(false) }
            )
        }
    }
}

@Composable
fun BentoSearchCard(
    viewModel: HomeViewModel,
    onSearchClick: (String, String) -> Unit
) {
    val tripType by viewModel.tripType.collectAsState()
    val origin by viewModel.origin.collectAsState()
    val destination by viewModel.destination.collectAsState()
    val departureDate by viewModel.departureDate.collectAsState()
    val returnDate by viewModel.returnDate.collectAsState()
    val adultsCount by viewModel.adultsCount.collectAsState()
    val childrenCount by viewModel.childrenCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Trip Type Selector
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLow, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), CircleShape)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (tripType == 0) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                        CircleShape
                    )
                    .clickable { viewModel.setTripType(0) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Ida y Vuelta",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (tripType == 0) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        if (tripType == 1) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                        CircleShape
                    )
                    .clickable { viewModel.setTripType(1) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Solo Ida",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (tripType == 1) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // From field
        SearchFieldItem(
            label = "Desde",
            value = origin.name,
            icon = Icons.Default.FlightTakeoff,
            onClick = { viewModel.toggleOriginSelector(true) }
        )

        // Swap Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { viewModel.swapAirports() },
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Intercambiar aeropuertos",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // To field
        SearchFieldItem(
            label = "Hacia",
            value = destination.name,
            icon = Icons.Default.FlightLand,
            onClick = { viewModel.toggleDestinationSelector(true) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dates
        val dateFormatter = remember { java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.getDefault()) }
        val departureText = remember(departureDate) { departureDate.format(dateFormatter) }
        val returnText = remember(returnDate) { returnDate.format(dateFormatter) }
        val datesText = if (tripType == 0) "$departureText - $returnText" else departureText
        SearchFieldItem(
            label = "Fechas",
            value = datesText,
            icon = Icons.Default.CalendarMonth,
            onClick = { viewModel.toggleDatePicker(true) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Travelers
        val travelersText = remember(adultsCount, childrenCount) {
            val adultsText = if (adultsCount == 1) "1 Adulto" else "$adultsCount Adultos"
            val childrenText = when (childrenCount) {
                0 -> ""
                1 -> ", 1 Niño"
                else -> ", $childrenCount Niños"
            }
            "$adultsText$childrenText"
        }
        SearchFieldItem(
            label = "Pasajeros",
            value = travelersText,
            icon = Icons.Default.Group,
            onClick = { viewModel.toggleTravelerSelector(true) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Button
        Button(
            onClick = { onSearchClick(origin.code, destination.code) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Buscar Vuelos",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun SearchFieldItem(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )
        // Capa interactiva invisible sobre el campo para capturar los clics de forma limpia
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clickable(onClick = onClick)
        )
    }
}

@Composable
fun QuickLinksGrid(
    onManageTripClick: () -> Unit,
    onCardClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickLinkCard(
                title = "Gestionar Viaje",
                icon = Icons.Default.Luggage,
                onClick = onManageTripClick,
                modifier = Modifier.weight(1f)
            )
            QuickLinkCard(
                title = "Check-In",
                icon = Icons.AutoMirrored.Filled.AirplaneTicket,
                onClick = { onCardClick("Check-In") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickLinkCard(
                title = "Flight Status",
                icon = Icons.Default.Flight,
                onClick = { onCardClick("Flight Status") },
                modifier = Modifier.weight(1f)
            )
            QuickLinkCard(
                title = "Help Center",
                icon = Icons.Default.SupportAgent,
                onClick = { onCardClick("Help Center") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickLinkCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class PromoDestination(
    val name: String,
    val price: String,
    val imageUrl: String,
    val gradientColor: Color
)

@Composable
fun ExploreDestinationsCarousel(onPromoClick: (String) -> Unit) {
    val items = remember {
        listOf(
            PromoDestination(
                name = "Tokio",
                price = "Vuelos desde €899",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDhM-pJ_i9j8R-AcrHYO_Swk8PC92v_CplAMboYVxgHt85jEZowJmL6p-R4X-xU2USkMiDDHMFzRbWlnAqKx_9RAzjr342j-9ULHpV0biFhWX7Tu7PwQDXV4n5DkQs_xtGXXJ_67-xmrfsERlX-w4jjTI8RBPCNmWHsl78_fAGjDm5MkjRYLjPtQPDFEhcHzD6sMhXgRtBRnsZJkjvdClXDMTqtw4-xesY9X1lHEynAoiI7h7oI2ou-DA1OtZ-JlC2JevR49crJ45jx",
                gradientColor = Color(0xFF526300)
            ),
            PromoDestination(
                name = "Alpes suizos",
                price = "Vuelos desde €149",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCTnzRyCTzNxEYnjac9bDYW5kEvxNgTl89V2HNnrxd_zeI9ulHZuZA2AbRwTEXMQ5lYtKnPjvlNSeaw66IE9_hsvwispaB09mj4DxjIhUQYxiM4fUt5lbbK6B-sTVaUkFNoowHwbIGseVWTBXqfr2DWuy0_-aqWsfDJDe56zX1fgv3pTRqoVxUafp2eq81E5LIsnI5Jz-v9q2MAmZv3TWANirKwRtpcZpOG7E2klvG9mKzJS1PMT7sglsf6nsOXuWprNNt56vFL50_w",
                gradientColor = Color(0xFF4B54A6)
            ),
            PromoDestination(
                name = "Roma",
                price = "Vuelos desde €89",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBRIFcKKL3aPjPkiOE-tO_sb8iPyCnkXytpKLoL3PVmE-Ho3u9STBaa9bKTX5KJ5d3ufrNA5Al4CzR2G2fSDC2rDCezwWnB_3XMfBp4rm6APig-MUDmzft0PuW_c2L7UC2V2r5Zbg2-YsL513kL0cPCxfR0_374Z_11q_zplcgI_QdWql7dSrSTXnptYGVjVVFTVu51heuKYMuDTUw3tpC7VBC9h9E2MfN10X88YUV_sAlEdrIkL2puWGZtxTdIPTdOmPslnRcPAEEG",
                gradientColor = Color(0xFF57633a)
            )
        )
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(items) { destination ->
            PromoCard(
                destination = destination,
                onClick = { onPromoClick(destination.name) }
            )
        }
    }
}

@Composable
fun PromoCard(
    destination: PromoDestination,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .height(240.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image via Coil AsyncImage
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = destination.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay with semantic tinting
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                destination.gradientColor.copy(alpha = 0.2f),
                                destination.gradientColor.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Text Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = destination.price,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = destination.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            // Navigation Button
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Explore",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// --- DIÁLOGOS AUXILIARES IMPLEMENTACIÓN ---

@Composable
fun AirportSelectorDialog(
    title: String,
    airports: List<AirportItem>,
    onDismiss: () -> Unit,
    onSelect: (AirportItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredAirports = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            airports
        } else {
            airports.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.code.contains(searchQuery, ignoreCase = true) ||
                it.country.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar aeropuerto o ciudad") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredAirports) { airport ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelect(airport) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = airport.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = airport.country,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = airport.code,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun TravelerSelectorDialog(
    adultsCount: Int,
    childrenCount: Int,
    onIncrementAdults: () -> Unit,
    onDecrementAdults: () -> Unit,
    onIncrementChildren: () -> Unit,
    onDecrementChildren: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Viajeros", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Adultos row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Adultos", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        Text("12+ años", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onDecrementAdults,
                            enabled = adultsCount > 1,
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Menos", modifier = Modifier.size(16.dp))
                        }
                        Text(adultsCount.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        IconButton(
                            onClick = onIncrementAdults,
                            enabled = adultsCount < 9,
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Más", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Niños row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Niños", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        Text("2-11 años", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onDecrementChildren,
                            enabled = childrenCount > 0,
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Menos", modifier = Modifier.size(16.dp))
                        }
                        Text(childrenCount.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        IconButton(
                            onClick = onIncrementChildren,
                            enabled = childrenCount < 9,
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Más", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = CircleShape
            ) {
                Text("Listo")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogComponent(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = initialDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedLocalDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(selectedLocalDate)
                    }
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialogComponent(
    initialStartDate: LocalDate,
    initialEndDate: LocalDate,
    onDatesSelected: (LocalDate, LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val zoneId = java.time.ZoneId.systemDefault()
    val initialStartMillis = initialStartDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
    val initialEndMillis = initialEndDate.atStartOfDay(zoneId).toInstant().toEpochMilli()

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartMillis,
        initialSelectedEndDateMillis = initialEndMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val startMillis = dateRangePickerState.selectedStartDateMillis
                    val endMillis = dateRangePickerState.selectedEndDateMillis
                    if (startMillis != null && endMillis != null) {
                        val startDate = java.time.Instant.ofEpochMilli(startMillis).atZone(zoneId).toLocalDate()
                        val endDate = java.time.Instant.ofEpochMilli(endMillis).atZone(zoneId).toLocalDate()
                        onDatesSelected(startDate, endDate)
                    }
                },
                enabled = dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = { Text("Selecciona fechas", modifier = Modifier.padding(16.dp)) },
            headline = { Text("Rango de viaje", modifier = Modifier.padding(16.dp)) },
            modifier = Modifier.weight(1f)
        )
    }
}
