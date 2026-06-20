package com.alalodev.skylink.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()

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
                    IconButton(onClick = { /* TODO */ }) {
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
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.TravelExplore, contentDescription = "My Trips") },
                    label = { Text("My Trips") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
                    label = { Text("Alerts") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
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
                .padding(horizontal = 16.dp, vertical = 20.dp), // 16dp horizontal safety margin
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
                        text = "Where to next?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )

                    BentoSearchCard()
                }
            }

            // Quick Links Grid Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Quick Services",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                QuickLinksGrid()
            }

            // Explore Destinations Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explore Destinations",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { /* TODO */ }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "View All", 
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

                ExploreDestinationsCarousel()
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BentoSearchCard() {
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
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Round Trip",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "One Way",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // From field
        SearchFieldItem(label = "From", value = "New York (JFK)", icon = Icons.Default.FlightTakeoff)

        // Swap Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap airports",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // To field
        SearchFieldItem(label = "To", value = "London (LHR)", icon = Icons.Default.FlightLand)

        Spacer(modifier = Modifier.height(8.dp))

        // Dates
        SearchFieldItem(label = "Dates", value = "Oct 12 - Oct 19", icon = Icons.Default.CalendarMonth)

        Spacer(modifier = Modifier.height(8.dp))

        // Travelers
        SearchFieldItem(label = "Travelers", value = "1 Adult", icon = Icons.Default.Group)

        Spacer(modifier = Modifier.height(16.dp))

        // Search Button
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CircleShape, // Primary Search Button fully pill-shaped
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
                    text = "Search Flights",
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
    modifier: Modifier = Modifier
) {
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
        modifier = modifier.fillMaxWidth(),
        readOnly = true
    )
}

@Composable
fun QuickLinksGrid() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickLinkCard(
                title = "Manage Trip",
                icon = Icons.Default.Luggage,
                modifier = Modifier.weight(1f)
            )
            QuickLinkCard(
                title = "Check-In",
                icon = Icons.AutoMirrored.Filled.AirplaneTicket,
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
                modifier = Modifier.weight(1f)
            )
            QuickLinkCard(
                title = "Help Center",
                icon = Icons.Default.SupportAgent,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickLinkCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { /* TODO */ },
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp), // 24px corner radius per DESIGN.md
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
fun ExploreDestinationsCarousel() {
    val items = remember {
        listOf(
            PromoDestination(
                name = "Tokyo",
                price = "Flights from $199",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDhM-pJ_i9j8R-AcrHYO_Swk8PC92v_CplAMboYVxgHt85jEZowJmL6p-R4X-xU2USkMiDDHMFzRbWlnAqKx_9RAzjr342j-9ULHpV0biFhWX7Tu7PwQDXV4n5DkQs_xtGXXJ_67-xmrfsERlX-w4jjTI8RBPCNmWHsl78_fAGjDm5MkjRYLjPtQPDFEhcHzD6sMhXgRtBRnsZJkjvdClXDMTqtw4-xesY9X1lHEynAoiI7h7oI2ou-DA1OtZ-JlC2JevR49crJ45jx",
                gradientColor = Color(0xFF526300) // Tertiary Olive Gold
            ),
            PromoDestination(
                name = "Swiss Alps",
                price = "Flights from $249",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCTnzRyCTzNxEYnjac9bDYW5kEvxNgTl89V2HNnrxd_zeI9ulHZuZA2AbRwTEXMQ5lYtKnPjvlNSeaw66IE9_hsvwispaB09mj4DxjIhUQYxiM4fUt5lbbK6B-sTVaUkFNoowHwbIGseVWTBXqfr2DWuy0_-aqWsfDJDe56zX1fgv3pTRqoVxUafp2eq81E5LIsnI5Jz-v9q2MAmZv3TWANirKwRtpcZpOG7E2klvG9mKzJS1PMT7sglsf6nsOXuWprNNt56vFL50_w",
                gradientColor = Color(0xFF4B54A6) // Primary Periwinkle
            ),
            PromoDestination(
                name = "Rome",
                price = "Flights from $399",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBRIFcKKL3aPjPkiOE-tO_sb8iPyCnkXytpKLoL3PVmE-Ho3u9STBaa9bKTX5KJ5d3ufrNA5Al4CzR2G2fSDC2rDCezwWnB_3XMfBp4rm6APig-MUDmzft0PuW_c2L7UC2V2r5Zbg2-YsL513kL0cPCxfR0_374Z_11q_zplcgI_QdWql7dSrSTXnptYGVjVVFTVu51heuKYMuDTUw3tpC7VBC9h9E2MfN10X88YUV_sAlEdrIkL2puWGZtxTdIPTdOmPslnRcPAEEG",
                gradientColor = Color(0xFF57633a) // Secondary Sage Green
            )
        )
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(items) { destination ->
            PromoCard(destination = destination)
        }
    }
}

@Composable
fun PromoCard(
    destination: PromoDestination,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .height(240.dp),
        shape = RoundedCornerShape(24.dp), // 24px corner radius per DESIGN.md
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
                onClick = { /* TODO */ },
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
