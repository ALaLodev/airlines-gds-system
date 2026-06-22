package com.alalodev.skylink.features.booking.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alalodev.skylink.core.network.util.NetworkResult
import com.alalodev.skylink.features.home.data.model.Flight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutPaymentScreen(
    flightId: String,
    seatNumber: String,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val flightState by viewModel.flightState.collectAsState()
    val bookingResult by viewModel.bookingResultState.collectAsState()
    val passengerName = viewModel.passengerName

    var cardholderName by remember(passengerName) { mutableStateOf(passengerName) }
    var cardNumber by remember { mutableStateOf("4532 7182 9381 0293") }
    var expiry by remember { mutableStateOf("12/30") }
    var cvv by remember { mutableStateOf("382") }
    var saveCard by remember { mutableStateOf(false) }

    var selectedMethod by remember { mutableStateOf("card") } // "card", "apple", "google"

    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var cardValidationFailed by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(flightId) {
        viewModel.loadFlight(flightId)
    }

    LaunchedEffect(bookingResult) {
        when (val result = bookingResult) {
            is NetworkResult.Success -> {
                isProcessing = false
                viewModel.clearBookingResult()
                showSuccessDialog = true
            }
            is NetworkResult.Error -> {
                isProcessing = false
                showErrorDialog = true
                viewModel.clearBookingResult()
            }
            is NetworkResult.Loading -> {
                isProcessing = true
            }
            null -> {
                isProcessing = false
            }
        }
    }

    Scaffold(
        containerColor = StitchBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("SkyLink", fontWeight = FontWeight.ExtraBold, color = StitchPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = StitchPrimary
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
                        Text(text = "Error al cargar el resumen de pago", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadFlight(flightId) }) { Text("Reintentar") }
                    }
                }
                is NetworkResult.Success -> {
                    val flight = state.data
                    CheckoutPaymentContent(
                        flight = flight,
                        seatNumber = seatNumber,
                        cardholderName = cardholderName,
                        cardNumber = cardNumber,
                        expiry = expiry,
                        cvv = cvv,
                        saveCard = saveCard,
                        selectedMethod = selectedMethod,
                        isProcessing = isProcessing,
                        onCardholderNameChange = { cardholderName = it },
                        onCardNumberChange = { cardNumber = it },
                        onExpiryChange = { expiry = it },
                        onCvvChange = { cvv = it },
                        onSaveCardChange = { saveCard = it },
                        onMethodChange = { selectedMethod = it },
                        onPayClick = {
                            val sanitizedCardNumber = cardNumber.replace(" ", "")
                            if (sanitizedCardNumber != "4532718293810293") {
                                cardValidationFailed = true
                            } else {
                                isProcessing = true
                                scope.launch {
                                    delay(1000) // Simulación de procesamiento de 1 segundo
                                    val basePrice = flight.price
                                    val rowNum = seatNumber.filter { it.isDigit() }.toIntOrNull() ?: 12
                                    val seatPrice = if (seatNumber != "none") {
                                        if (rowNum in 1..3) 23.0 else 3.0
                                    } else 0.0
                                    val taxes = 42.50
                                    val total = basePrice + seatPrice + taxes
                                    val cabinClass = if (rowNum in 1..3) "BUSINESS" else "ECONOMY"

                                    viewModel.bookFlight(
                                        flightId = flightId,
                                        totalAmount = total,
                                        seatNumber = seatNumber,
                                        cabinClass = cabinClass
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // Procesando Loading Overlay
            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) {}, // Bloquea clics
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = StitchPrimary)
                            Text("Procesando pago seguro...", fontWeight = FontWeight.SemiBold, color = StitchOnSurface)
                        }
                    }
                }
            }

            // Diálogo de Éxito (Verde)
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = {},
                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                onPaymentSuccess()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StitchPrimary)
                        ) {
                            Text("Aceptar", color = Color.White)
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(28.dp)
                            )
                            Text("¡Compra Exitosa!", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Text(
                            text = "Su reserva se ha realizado correctamente. Puede consultar sus vuelos comprados en la sección 'Mis Viajes'.",
                            fontSize = 15.sp,
                            color = StitchOnSurface
                        )
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // Diálogo de Error (Rojo)
            if (cardValidationFailed || showErrorDialog) {
                AlertDialog(
                    onDismissRequest = {
                        cardValidationFailed = false
                        showErrorDialog = false
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                cardValidationFailed = false
                                showErrorDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                        ) {
                            Text("Cerrar", color = Color.White)
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Error",
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(28.dp)
                            )
                            Text("Error en el Pago", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Text(
                            text = "Los datos de la tarjeta no son correctos. Por favor, verifícalos e inténtalo de nuevo.",
                            fontSize = 15.sp,
                            color = StitchOnSurface
                        )
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun CheckoutPaymentContent(
    flight: Flight,
    seatNumber: String,
    cardholderName: String,
    cardNumber: String,
    expiry: String,
    cvv: String,
    saveCard: Boolean,
    selectedMethod: String,
    isProcessing: Boolean,
    onCardholderNameChange: (String) -> Unit,
    onCardNumberChange: (String) -> Unit,
    onExpiryChange: (String) -> Unit,
    onCvvChange: (String) -> Unit,
    onSaveCardChange: (Boolean) -> Unit,
    onMethodChange: (String) -> Unit,
    onPayClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val basePrice = flight.price
    val seatPrice = if (seatNumber != "none") {
        val row = seatNumber.filter { it.isDigit() }.toIntOrNull() ?: 12
        if (row in 1..3) 23.0 else 3.0
    } else 0.0
    val taxes = 42.50
    val total = basePrice + seatPrice + taxes

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Breadcrumb Progress Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info Step
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(StitchOutlineVariant.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = StitchOutlineVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(text = "INFO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StitchOutlineVariant)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(StitchOutlineVariant.copy(alpha = 0.3f))
                        .padding(bottom = 8.dp)
                )

                // Seats Step
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(StitchOutlineVariant.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = StitchOutlineVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(text = "ASIENTOS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StitchOutlineVariant)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(StitchPrimary.copy(alpha = 0.3f))
                        .padding(bottom = 8.dp)
                )

                // Payment Step
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(StitchPrimary, CircleShape)
                            .border(4.dp, StitchPrimaryContainer.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(text = "PAGO", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = StitchPrimary)
                }
            }

            // Section 1: Order Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StitchSurfaceContainerLow),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    StitchOutlineVariant.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "RESUMEN DE COMPRA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = StitchOnSurface.copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Vuelo de Ida (${flight.origin} - ${flight.destination})", fontSize = 14.sp)
                        Text(text = String.format(Locale.getDefault(), "€%.2f", basePrice), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    if (seatNumber != "none") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Selección de Asiento ($seatNumber)", fontSize = 14.sp)
                            Text(
                                text = String.format(Locale.getDefault(), "+€%.2f", seatPrice),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = StitchSecondary
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Tasas Aeroportuarias", fontSize = 14.sp)
                        Text(text = String.format(Locale.getDefault(), "€%.2f", taxes), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    HorizontalDivider(color = StitchOutlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(text = "Importe Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = String.format(Locale.getDefault(), "€%.2f", total),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = StitchPrimary
                        )
                    }
                }
            }

            // Section 2: Payment Method Selection
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "MÉTODO DE PAGO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = StitchOnSurface.copy(alpha = 0.5f),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Credit Card Option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedMethod == "card") StitchPrimaryContainer else StitchSurfaceContainerLow)
                            .border(
                                width = 2.dp,
                                color = if (selectedMethod == "card") StitchPrimary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onMethodChange("card") }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = if (selectedMethod == "card") StitchOnPrimaryContainer else StitchOnSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Tarjeta",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMethod == "card") StitchOnPrimaryContainer else StitchOnSurface
                            )
                        }
                    }

                    // Apple Pay Option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedMethod == "apple") StitchPrimaryContainer else StitchSurfaceContainerLow)
                            .border(
                                width = 2.dp,
                                color = if (selectedMethod == "apple") StitchPrimary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onMethodChange("apple") }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = null,
                                tint = if (selectedMethod == "apple") StitchOnPrimaryContainer else StitchOnSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Apple Pay",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMethod == "apple") StitchOnPrimaryContainer else StitchOnSurface
                            )
                        }
                    }

                    // Google Pay Option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedMethod == "google") StitchPrimaryContainer else StitchSurfaceContainerLow)
                            .border(
                                width = 2.dp,
                                color = if (selectedMethod == "google") StitchPrimary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onMethodChange("google") }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (selectedMethod == "google") StitchOnPrimaryContainer else StitchOnSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Google Pay",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMethod == "google") StitchOnPrimaryContainer else StitchOnSurface
                            )
                        }
                    }
                }
            }

            // Section 3: Credit Card Input Form
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StitchSurfaceContainerLow)
                    .border(1.dp, StitchOutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Cardholder Name
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Titular de la Tarjeta",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = StitchOnSurface.copy(alpha = 0.7f)
                        )
                        OutlinedTextField(
                            value = cardholderName,
                            onValueChange = onCardholderNameChange,
                            placeholder = { Text("Alexander Mitchell", color = StitchOutlineVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StitchPrimary,
                                unfocusedBorderColor = StitchOutlineVariant.copy(alpha = 0.4f),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Card Number
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Número de Tarjeta",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = StitchOnSurface.copy(alpha = 0.7f)
                        )
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = onCardNumberChange,
                            placeholder = { Text("0000 0000 0000 0000", color = StitchOutlineVariant) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = StitchOutlineVariant
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StitchPrimary,
                                unfocusedBorderColor = StitchOutlineVariant.copy(alpha = 0.4f),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Expiry & CVV Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Vencimiento",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = StitchOnSurface.copy(alpha = 0.7f)
                            )
                            OutlinedTextField(
                                value = expiry,
                                onValueChange = onExpiryChange,
                                placeholder = { Text("MM / YY", color = StitchOutlineVariant) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = StitchPrimary,
                                    unfocusedBorderColor = StitchOutlineVariant.copy(alpha = 0.4f),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "CVV",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = StitchOnSurface.copy(alpha = 0.7f)
                            )
                            OutlinedTextField(
                                value = cvv,
                                onValueChange = onCvvChange,
                                placeholder = { Text("***", color = StitchOutlineVariant) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = StitchPrimary,
                                    unfocusedBorderColor = StitchOutlineVariant.copy(alpha = 0.4f),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Checkbox(
                            checked = saveCard,
                            onCheckedChange = onSaveCardChange,
                            colors = CheckboxDefaults.colors(checkedColor = StitchPrimary)
                        )
                        Text(
                            text = "Guardar datos para futuros vuelos",
                            fontSize = 13.sp,
                            color = StitchOnSurface.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Security Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = StitchOutlineVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "PCI-DSS SECURE PAYMENT GATEWAY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StitchOutlineVariant,
                    letterSpacing = 1.5.sp
                )
            }
        }

        // Bottom Navigation Payment Bar
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
            Button(
                onClick = onPayClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = StitchPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(99.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = cardholderName.isNotBlank() && cardNumber.isNotBlank() && expiry.isNotBlank() && cvv.isNotBlank() && !isProcessing
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONFIRMAR Y PAGAR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "€%.2f", total),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
