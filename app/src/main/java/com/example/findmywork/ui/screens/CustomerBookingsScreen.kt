package com.example.findmywork.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.marketplace.BookingStatusChip
import com.example.findmywork.ui.components.marketplace.MarketplaceEmptyState
import com.example.findmywork.ui.components.marketplace.WorkerAvatar
import com.example.findmywork.ui.theme.*

@Composable
fun CustomerBookingsScreen(
    customerId: String = "cust_ronen",
    firestoreRepository: FirestoreRepository,
    onNavigateToBookingDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upcoming", "Ongoing", "Completed", "Cancelled")

    val bookings by firestoreRepository.getCustomerBookingsFlow(customerId).collectAsState(initial = emptyList())

    val filteredBookings = remember(bookings, selectedTabIndex) {
        when (selectedTabIndex) {
            0 -> bookings.filter { it.status.uppercase() in listOf("PENDING", "ACCEPTED") }
            1 -> bookings.filter { it.status.uppercase() in listOf("ON_THE_WAY", "ARRIVED", "STARTED", "WORKING") }
            2 -> bookings.filter { it.status.uppercase() in listOf("COMPLETED", "RATED") }
            3 -> bookings.filter { it.status.uppercase() == "CANCELLED" }
            else -> bookings
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FMWSurface)
            ) {
                Text(
                    text = "My Bookings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FMWTextPrimary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
                )
                Text(
                    text = "Track appointments and view service history",
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                )

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = FMWSurface,
                    contentColor = FMWNavy,
                    indicator = {},
                    divider = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(FMWSurfaceSubtle)
                        .padding(3.dp)
                ) {
                    tabs.forEachIndexed { index, tabName ->
                        val isSelected = selectedTabIndex == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) FMWSurface else Color.Transparent)
                                .clickable { selectedTabIndex = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) FMWNavy else FMWTextSecondary
                            )
                        }
                    }
                }
                HorizontalDivider(color = FMWBorder.copy(alpha = 0.5f))
            }
        },
        containerColor = FMWBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredBookings.isEmpty()) {
                item {
                    val message = when (selectedTabIndex) {
                        0 -> "You have no upcoming bookings scheduled."
                        1 -> "No active ongoing services at the moment."
                        2 -> "Completed service records will appear here."
                        else -> "No cancelled bookings."
                    }
                    MarketplaceEmptyState(
                        title = "No ${tabs[selectedTabIndex]} Bookings",
                        message = message,
                        icon = Icons.Rounded.EventAvailable,
                        actionText = "Find a Craftsman",
                        onAction = onNavigateToHome
                    )
                }
            } else {
                items(filteredBookings) { job ->
                    CustomerBookingCard(
                        job = job,
                        onClick = { onNavigateToBookingDetail(job.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerBookingCard(
    job: Job,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = FMWSurface,
        border = BorderStroke(1.dp, FMWBorder),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Category + Booking ID + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(FMWSoftBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.HomeRepairService,
                            contentDescription = null,
                            tint = FMWBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = job.categoryName.ifEmpty { "Service" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                        Text(
                            text = "Booking #${job.id.take(9)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = FMWMutedText
                        )
                    }
                }
                BookingStatusChip(status = job.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = FMWBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Sub-service & Notes
            if (job.subServiceName.isNotBlank()) {
                Text(
                    text = job.subServiceName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FMWTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Worker Assigned info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WorkerAvatar(name = job.workerName ?: "Pro", size = 38.dp, isVerified = true)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.workerName ?: "Assigned Technician",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )
                    Text(
                        text = "${job.bookingDate} • ${job.timeSlot}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWTextSecondary
                    )
                }

                Text(
                    text = formatInr(job.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWNavy
                )
            }
        }
    }
}
