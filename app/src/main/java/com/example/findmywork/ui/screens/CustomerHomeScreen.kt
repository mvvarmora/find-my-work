package com.example.findmywork.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.model.Job
import com.example.findmywork.data.model.Worker
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.marketplace.*
import com.example.findmywork.ui.theme.*

@Composable
fun CustomerHomeScreen(
    customerId: String?,
    customerName: String = "Ronen",
    firestoreRepository: FirestoreRepository,
    onNavigateToWorkerDetail: (String) -> Unit,
    onNavigateToBookingFlow: (String) -> Unit, // workerId
    onNavigateToBookingDetail: (String) -> Unit, // jobId
    onNavigateToCategories: () -> Unit,
    onNavigateToCategoryWorkers: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToWallet: () -> Unit,
    isDarkTheme: Boolean = false,
    onToggleTheme: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryChip by remember { mutableStateOf("All") }
    var selectedCity by remember { mutableStateOf("Rajkot") }
    var showCityDialog by remember { mutableStateOf(false) }

    val allWorkers by firestoreRepository.getAllWorkersFlow().collectAsState(initial = emptyList())
    val customerBookings by firestoreRepository.getCustomerBookingsFlow(customerId ?: "demo_customer")
        .collectAsState(initial = emptyList())

    val activeBooking = customerBookings.firstOrNull {
        it.status in listOf("PENDING", "ACCEPTED", "ON_THE_WAY", "ARRIVED", "STARTED")
    }

    val categoryList = listOf(
        "All", "Plumbing", "Electrical", "Carpentry", "Cleaning", "Painting", "AC Repair"
    )

    val quickGridCategories = listOf(
        Triple("Plumbing", Icons.Rounded.Plumbing, CategoryColors["plumbing"] ?: CategoryColor(Color(0xFFECFEFF), Color(0xFF0891B2))),
        Triple("Electrical", Icons.Rounded.Bolt, CategoryColors["electrical"] ?: CategoryColor(Color(0xFFFFFBEB), Color(0xFFD97706))),
        Triple("Carpentry", Icons.Rounded.Carpenter, CategoryColors["carpentry"] ?: CategoryColor(Color(0xFFFFF7ED), Color(0xFFEA580C))),
        Triple("Cleaning", Icons.Rounded.CleaningServices, CategoryColors["cleaning"] ?: CategoryColor(Color(0xFFEFF6FF), Color(0xFF2563EB))),
        Triple("Painting", Icons.Rounded.FormatPaint, CategoryColors["painting"] ?: CategoryColor(Color(0xFFFAF5FF), Color(0xFF7C3AED))),
        Triple("AC Service", Icons.Rounded.AcUnit, CategoryColors["ac_repair"] ?: CategoryColor(Color(0xFFF0F9FF), Color(0xFF0284C7))),
        Triple("Pest Control", Icons.Rounded.PestControl, CategoryColors["pest_control"] ?: CategoryColor(Color(0xFFF0FDF4), Color(0xFF16A34A))),
        Triple("More Services", Icons.Rounded.GridView, CategoryColor(Color(0xFFF1F5F9), Color(0xFF475569)))
    )

    val filteredWorkers = remember(allWorkers, searchQuery, selectedCategoryChip) {
        allWorkers.filter { worker ->
            val matchesSearch = searchQuery.isBlank() ||
                    worker.name.contains(searchQuery, ignoreCase = true) ||
                    worker.skills.any { it.contains(searchQuery, ignoreCase = true) } ||
                    worker.categoryIds.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesCategory = selectedCategoryChip == "All" ||
                    worker.categoryIds.any { it.contains(selectedCategoryChip, ignoreCase = true) }

            matchesSearch && matchesCategory
        }
    }

    val nearbyWorkers = allWorkers.take(4)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FMWBackground),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Sticky-like Header: Greeting, Location, Wallet, Notification
        item {
            MarketplaceTopBar(
                customerName = customerName,
                currentCity = selectedCity,
                walletBalance = 3564.0,
                hasUnreadNotifications = true,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onCityClick = { showCityDialog = true },
                onNotificationClick = onNavigateToNotifications,
                onWalletClick = onNavigateToWallet
            )
        }

        // Subtitle headline inspired by reference: "The easiest way to find trusted local craftsmen"
        item {
            PaddingValues(horizontal = 16.dp, vertical = 4.dp).let {
                Column(modifier = Modifier.padding(it)) {
                    Text(
                        text = "The easiest way to find trusted local craftsmen",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary,
                        lineHeight = 28.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 2. Search & Filter Bar
        item {
            MarketplaceSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = onNavigateToCategories
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 3. Community Trust Banner
        item {
            CommunityTrustBanner(onClick = { /* Trust Dialog / verification details */ })
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 4. Active Job Live Tracker Banner (if any ongoing booking)
        if (activeBooking != null) {
            item {
                Surface(
                    onClick = { onNavigateToBookingDetail(activeBooking.id) },
                    shape = RoundedCornerShape(18.dp),
                    color = FMWSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FMWBlue.copy(alpha = 0.3f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FMWSoftBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DirectionsCar,
                                contentDescription = null,
                                tint = FMWBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Active Booking",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FMWTextPrimary
                                )
                                BookingStatusChip(status = activeBooking.status)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${activeBooking.categoryName} • ${activeBooking.workerName ?: "Assigned Worker"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = FMWTextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = "Track",
                            tint = FMWNavy
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // 5. Category Chips Row
        item {
            CategoryChipRow(
                categories = categoryList,
                selectedCategory = selectedCategoryChip,
                onSelectCategory = { selectedCategoryChip = it }
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 6. Quick Category Icons Grid (4-column)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Popular Services",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                quickGridCategories.chunked(4).forEach { rowList ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowList.forEach { (catName, icon, colorPair) ->
                            CategoryTile(
                                title = catName,
                                icon = icon,
                                colorPair = colorPair,
                                onClick = {
                                    if (catName == "More Services") {
                                        onNavigateToCategories()
                                    } else {
                                        onNavigateToCategoryWorkers(catName)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 7. Nearby Workers Section (Horizontal Carousel)
        if (nearbyWorkers.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Popular Near You",
                    subtitle = "Ready to help near you",
                    actionText = "See All",
                    onActionClick = onNavigateToCategories
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(nearbyWorkers) { worker ->
                        NearbyWorkerCard(
                            worker = worker,
                            professionTitle = worker.skills.firstOrNull() ?: "Specialist",
                            distanceText = "1.2 km away",
                            ratingText = if (worker.ratingCount > 0)
                                "%.1f (%d)".format(worker.ratingSum / worker.ratingCount, worker.ratingCount)
                            else "4.8 (90)",
                            onClick = { onNavigateToWorkerDetail(worker.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))
            }
        }

        // 8. Top Rated Experts Section (Vertical Cards)
        item {
            SectionHeader(
                title = "Top Rated Experts",
                subtitle = "Highest rated professionals in $selectedCity",
                actionText = null
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (filteredWorkers.isEmpty()) {
            item {
                MarketplaceEmptyState(
                    title = "No Workers Found",
                    message = "We couldn't find any professionals matching '$searchQuery'. Try adjusting your filters.",
                    actionText = "Clear Filters",
                    onAction = {
                        searchQuery = ""
                        selectedCategoryChip = "All"
                    }
                )
            }
        } else {
            items(filteredWorkers) { worker ->
                val primarySkill = worker.skills.firstOrNull() ?: "${worker.categoryIds.firstOrNull() ?: "General"} Expert"
                val price = worker.pricing?.let { "₹${it.toInt()} / service" } ?: "₹500 / service"

                WorkerCard(
                    worker = worker,
                    professionTitle = primarySkill,
                    distanceText = "${(1..5).random()}.${(0..9).random()} km away",
                    priceText = price,
                    onCardClick = { onNavigateToWorkerDetail(worker.id) },
                    onBookClick = { onNavigateToBookingFlow(worker.id) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }

    // City Selection Dialog
    if (showCityDialog) {
        val cities = listOf("Rajkot", "Ahmedabad", "Surat", "Vadodara", "Mumbai", "Delhi")
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = {
                Text("Select Your Location", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    cities.forEach { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedCity = city
                                    showCityDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                tint = if (city == selectedCity) FMWOrange else FMWTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = city,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (city == selectedCity) FontWeight.Bold else FontWeight.Normal,
                                color = if (city == selectedCity) FMWOrange else FMWTextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("Close", fontWeight = FontWeight.Bold, color = FMWNavy)
                }
            }
        )
    }
}
