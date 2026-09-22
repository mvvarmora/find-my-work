package com.example.findmywork.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.model.Job
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.marketplace.BookingStatusChip
import com.example.findmywork.ui.components.marketplace.RatingBadge
import com.example.findmywork.ui.components.marketplace.WorkerAvatar
import com.example.findmywork.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CustomerBookingDetailScreen(
    jobId: String,
    firestoreRepository: FirestoreRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val jobFlow = remember(jobId) { firestoreRepository.getJobByIdFlow(jobId) }
    val job by jobFlow.collectAsState(initial = null)

    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }
    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedStars by remember { mutableIntStateOf(5) }
    var reviewFeedback by remember { mutableStateOf("") }

    val activeJob = job ?: Job(
        id = jobId,
        customerName = "Ronen",
        workerName = "Johan Thomas",
        workerPhone = "+91 98251 10001",
        workerRating = 4.9,
        categoryName = "Plumbing",
        subServiceName = "Pipe Leakage & Tap Replacement",
        flatNo = "A-304, Royal Palms",
        landmark = "Near 150 Feet Ring Road",
        city = "Rajkot",
        basePrice = 500.0,
        platformFee = 40.0,
        gstAmount = 24.0,
        totalAmount = 564.0,
        bookingDate = "Today, 18 Sep",
        timeSlot = "13:00 - 15:00",
        specialInstructions = "Kitchen sink pipe is leaking under cabinet. Bring replacement valve.",
        status = "ON_THE_WAY"
    )

    val timelineSteps = listOf(
        "Confirmed" to "Booking Confirmed",
        "Accepted" to "Worker Accepted",
        "On The Way" to "Worker On The Way",
        "Arrived" to "Arrived at Location",
        "Working" to "Job in Progress",
        "Completed" to "Service Completed"
    )

    val currentStepIndex = when (activeJob.status.uppercase()) {
        "PENDING" -> 0
        "ACCEPTED" -> 1
        "ON_THE_WAY" -> 2
        "ARRIVED" -> 3
        "STARTED", "WORKING" -> 4
        "COMPLETED", "RATED" -> 5
        else -> 0
    }

    Scaffold(
        topBar = {
            Surface(
                color = FMWSurface,
                border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = FMWTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Booking Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                    }

                    BookingStatusChip(
                        status = activeJob.status,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        },
        containerColor = FMWBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Booking ID Bar
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = FMWSoftBlue,
                border = BorderStroke(1.dp, FMWBlue.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Booking ID: #${activeJob.id.take(10)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavy
                    )
                    Text(
                        text = activeJob.bookingDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWBlue
                    )
                }
            }

            // 2. Visual Status Stepper Timeline
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = FMWSurface,
                border = BorderStroke(1.dp, FMWBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Booking Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    timelineSteps.forEachIndexed { index, (shortLabel, fullLabel) ->
                        val isFinished = index <= currentStepIndex
                        val isCurrent = index == currentStepIndex

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circle indicator
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> FMWOrange
                                            isFinished -> FMWBlue
                                            else -> FMWSurfaceSubtle
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (isFinished) Color.Transparent else FMWBorder,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isFinished && !isCurrent) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = fullLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrent) FontWeight.Bold else if (isFinished) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isCurrent) FMWOrange else if (isFinished) FMWTextPrimary else FMWMutedText
                            )
                        }

                        if (index < timelineSteps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 11.dp)
                                    .width(2.dp)
                                    .height(18.dp)
                                    .background(if (index < currentStepIndex) FMWBlue else FMWBorder)
                            )
                        }
                    }
                }
            }

            // 3. Worker Assigned Card with Quick Call & WhatsApp
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = FMWSurface,
                border = BorderStroke(1.dp, FMWBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WorkerAvatar(name = activeJob.workerName ?: "Technician", size = 56.dp, isVerified = true)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeJob.workerName ?: "Assigned Technician",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Text(
                                text = "${activeJob.categoryName} Specialist",
                                style = MaterialTheme.typography.bodySmall,
                                color = FMWTextSecondary
                            )
                            RatingBadge(
                                rating = "%.1f".format(activeJob.workerRating ?: 4.9),
                                reviewCount = 120
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = FMWBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Call & Message buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val phone = activeJob.workerPhone ?: "+919825110001"
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FMWNavy,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call Worker", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val phone = (activeJob.workerPhone ?: "+919825110001").replace(" ", "")
                                val url = "https://api.whatsapp.com/send?phone=$phone&text=Hello%2C%20regarding%20booking%20${activeJob.id}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, FMWSuccess),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Chat, contentDescription = null, tint = FMWSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp", color = FMWSuccess, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 4. Service Address & Schedule Details
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = FMWSurface,
                border = BorderStroke(1.dp, FMWBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Appointment Schedule & Address",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(
                        icon = Icons.Rounded.Event,
                        label = "Date & Time",
                        value = "${activeJob.bookingDate} (${activeJob.timeSlot})"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        icon = Icons.Rounded.LocationOn,
                        label = "Location",
                        value = "${activeJob.flatNo}, ${activeJob.landmark}, ${activeJob.city}"
                    )
                    if (activeJob.specialInstructions.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailRow(
                            icon = Icons.Rounded.Description,
                            label = "Notes",
                            value = activeJob.specialInstructions
                        )
                    }
                }
            }

            // 5. Cost Breakdown Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = FMWSurface,
                border = BorderStroke(1.dp, FMWBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cost Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(activeJob.subServiceName.ifEmpty { "Service Fee" }, color = FMWTextSecondary)
                        Text(formatInr(activeJob.basePrice), fontWeight = FontWeight.SemiBold, color = FMWTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Safety & Platform Fee", color = FMWTextSecondary)
                        Text(formatInr(activeJob.platformFee), fontWeight = FontWeight.SemiBold, color = FMWTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Taxes & GST (18%)", color = FMWTextSecondary)
                        Text(formatInr(activeJob.gstAmount), fontWeight = FontWeight.SemiBold, color = FMWTextPrimary)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = FMWBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Amount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FMWTextPrimary)
                        Text(formatInr(activeJob.totalAmount), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FMWOrange)
                    }
                }
            }

            // 6. Action: Cancel or Rate
            if (activeJob.status in listOf("PENDING", "ACCEPTED")) {
                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, FMWError),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel Booking", color = FMWError, fontWeight = FontWeight.Bold)
                }
            } else if (activeJob.status in listOf("COMPLETED", "RATED")) {
                Button(
                    onClick = { showRatingDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FMWOrange,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rate & Review Worker", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Cancel Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Appointment?", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Are you sure you want to cancel this booking? Please tell us why:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        placeholder = { Text("Reason for cancellation...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            firestoreRepository.cancelCustomerBooking(activeJob.id, cancelReason)
                            showCancelDialog = false
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FMWError)
                ) {
                    Text("Cancel Booking", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Booking", color = FMWNavy)
                }
            }
        )
    }

    // Rating Dialog
    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Rate ${activeJob.workerName}", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("How was your experience with the service?")
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = if (star <= selectedStars) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                contentDescription = "$star stars",
                                tint = FMWStarYellow,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { selectedStars = star }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = reviewFeedback,
                        onValueChange = { reviewFeedback = it },
                        placeholder = { Text("Write a short review...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            firestoreRepository.submitReview(
                                jobId = activeJob.id,
                                workerId = activeJob.workerId ?: "",
                                rating = selectedStars,
                                comment = reviewFeedback,
                                customerName = activeJob.customerName
                            )
                            showRatingDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FMWNavy)
                ) {
                    Text("Submit Review", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("Cancel", color = FMWTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = FMWBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = FMWTextSecondary)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = FMWTextPrimary)
        }
    }
}
