package com.example.findmywork.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.model.Job
import com.example.findmywork.data.model.Worker
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.marketplace.*
import com.example.findmywork.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun BookingFlowScreen(
    workerId: String,
    customerId: String = "cust_ronen",
    customerName: String = "Ronen",
    firestoreRepository: FirestoreRepository,
    onNavigateBack: () -> Unit,
    onBookingSuccess: (String) -> Unit, // jobId
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val worker by firestoreRepository.getWorkerFlow(workerId).collectAsState(initial = null)

    val activeWorker = worker ?: Worker(
        id = workerId,
        name = "Johan Thomas",
        phone = "+91 98251 10001",
        categoryIds = listOf("plumbing"),
        pricing = 500.0,
        city = "Rajkot",
        ratingSum = 588.0,
        ratingCount = 120
    )

    var currentStep by remember { mutableIntStateOf(1) } // 1: Service, 2: Date & Time, 3: Confirm

    // Step 1 states
    val subServices = listOf(
        "Standard Inspection & Diagnosis",
        "Leakage Repair & Pipe Fitting",
        "Fixture / Tap / Valve Replacement",
        "Complete Service Overhaul"
    )
    var selectedSubService by remember { mutableStateOf(subServices[0]) }
    var problemNote by remember { mutableStateOf("") }

    // Step 2 states
    val dateItems = remember {
        listOf(
            BookingDateItem("MON", "18", "2026-09-18"),
            BookingDateItem("TUE", "19", "2026-09-19"),
            BookingDateItem("WED", "20", "2026-09-20"),
            BookingDateItem("THU", "21", "2026-09-21"),
            BookingDateItem("FRI", "22", "2026-09-22"),
            BookingDateItem("SAT", "23", "2026-09-23")
        )
    }
    var selectedDate by remember { mutableStateOf(dateItems[1].fullDateString) } // TUE 19 default matching screenshot
    val timeSlots = listOf(
        "09:00 - 11:00",
        "11:30 - 13:30",
        "14:00 - 16:00",
        "16:30 - 18:30"
    )
    var selectedTimeSlot by remember { mutableStateOf(timeSlots[2]) } // 14:00 - 16:00 matching screenshot

    // Step 3 states
    var flatAddress by remember { mutableStateOf("A-304, Royal Palms") }
    var landmark by remember { mutableStateOf("Near 150 Feet Ring Road") }
    var city by remember { mutableStateOf("Rajkot") }
    var pinCode by remember { mutableStateOf("360005") }
    var paymentOption by remember { mutableStateOf("Pay after Service") }
    var isSubmitting by remember { mutableStateOf(false) }

    val basePrice = activeWorker.pricing ?: 500.0
    val platformFee = 40.0
    val gst = 24.0
    val totalAmount = basePrice + platformFee + gst

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FMWSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = FMWTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Book Appointment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )
                }

                // 3-Step Visual Progress Stepper
                BookingStepProgress(currentStep = currentStep)
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = FMWBorder.copy(alpha = 0.5f))
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp),
                color = FMWSurface,
                border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estimated Total",
                            style = MaterialTheme.typography.bodySmall,
                            color = FMWTextSecondary
                        )
                        Text(
                            text = formatInr(totalAmount),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = FMWNavy
                        )
                    }

                    Button(
                        onClick = {
                            if (currentStep < 3) {
                                currentStep++
                            } else {
                                isSubmitting = true
                                scope.launch {
                                    val newJob = Job(
                                        customerId = customerId,
                                        customerName = customerName,
                                        customerPhone = "+91 98250 99999",
                                        workerId = activeWorker.id,
                                        workerName = activeWorker.name,
                                        workerPhone = activeWorker.phone,
                                        workerRating = if (activeWorker.ratingCount > 0)
                                            activeWorker.ratingSum / activeWorker.ratingCount else 4.9,
                                        flatNo = flatAddress,
                                        landmark = landmark,
                                        city = city,
                                        pinCode = pinCode,
                                        categoryId = activeWorker.categoryIds.firstOrNull() ?: "general",
                                        categoryName = activeWorker.skills.firstOrNull() ?: "Service",
                                        subServiceName = selectedSubService,
                                        basePrice = basePrice,
                                        platformFee = platformFee,
                                        gstAmount = gst,
                                        totalAmount = totalAmount,
                                        bookingDate = selectedDate,
                                        timeSlot = selectedTimeSlot,
                                        specialInstructions = problemNote,
                                        status = "PENDING"
                                    )
                                    val createdJobId = firestoreRepository.createCustomerBooking(newJob)
                                    isSubmitting = false
                                    onBookingSuccess(createdJobId)
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FMWOrange,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (currentStep == 3) "Confirm Booking" else "Continue",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Worker Preview Banner
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = FMWSurface,
                border = BorderStroke(1.dp, FMWBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WorkerAvatar(name = activeWorker.name, size = 52.dp, isVerified = true)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeWorker.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            VerificationBadge()
                        }
                        Text(
                            text = activeWorker.skills.firstOrNull() ?: "Specialist Pro",
                            style = MaterialTheme.typography.bodySmall,
                            color = FMWTextSecondary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = FMWStarYellow,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "4.9 (120 reviews)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = FMWTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Animated Step Switcher
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "BookingStepAnimation"
            ) { step ->
                when (step) {
                    1 -> { // Step 1: Select Service & Notes
                        Column {
                            Text(
                                text = "1. Select Specific Service",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            subServices.forEach { sub ->
                                val isSelected = sub == selectedSubService
                                Surface(
                                    onClick = { selectedSubService = sub },
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) FMWSoftBlue else FMWSurface,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) FMWBlue else FMWBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedSubService = sub },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = FMWBlue,
                                                unselectedColor = FMWTextSecondary
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = sub,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) FMWNavy else FMWTextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Describe The Problem (Optional)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = problemNote,
                                onValueChange = { problemNote = it },
                                placeholder = {
                                    Text("E.g. Kitchen tap is leaking heavily, need replacement valve...")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FMWBlue,
                                    unfocusedBorderColor = FMWBorder,
                                    focusedContainerColor = FMWSurface,
                                    unfocusedContainerColor = FMWSurface
                                )
                            )
                        }
                    }
                    2 -> { // Step 2: Date & Time Picker (Matching Screenshot!)
                        Column {
                            Text(
                                text = "Select a Date",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            DateSelectorStrip(
                                dates = dateItems,
                                selectedDate = selectedDate,
                                onSelectDate = { selectedDate = it }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Available Time Slots",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            TimeSlotGrid(
                                timeSlots = timeSlots,
                                selectedSlot = selectedTimeSlot,
                                onSelectSlot = { selectedTimeSlot = it }
                            )
                        }
                    }
                    3 -> { // Step 3: Address & Confirmation
                        Column {
                            Text(
                                text = "Service Address",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = flatAddress,
                                onValueChange = { flatAddress = it },
                                label = { Text("Flat / House No / Society") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FMWBlue,
                                    unfocusedBorderColor = FMWBorder,
                                    focusedContainerColor = FMWSurface,
                                    unfocusedContainerColor = FMWSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = landmark,
                                onValueChange = { landmark = it },
                                label = { Text("Landmark") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FMWBlue,
                                    unfocusedBorderColor = FMWBorder,
                                    focusedContainerColor = FMWSurface,
                                    unfocusedContainerColor = FMWSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("City") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FMWBlue,
                                        unfocusedBorderColor = FMWBorder,
                                        focusedContainerColor = FMWSurface,
                                        unfocusedContainerColor = FMWSurface
                                    )
                                )
                                OutlinedTextField(
                                    value = pinCode,
                                    onValueChange = { pinCode = it },
                                    label = { Text("PIN Code") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FMWBlue,
                                        unfocusedBorderColor = FMWBorder,
                                        focusedContainerColor = FMWSurface,
                                        unfocusedContainerColor = FMWSurface
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Cost Breakdown Card
                            Text(
                                text = "Cost Summary",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = FMWSurface,
                                border = BorderStroke(1.dp, FMWBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    CostRow(title = "Service Charge ($selectedSubService)", amount = formatInr(basePrice))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    CostRow(title = "Safety & Platform Fee", amount = formatInr(platformFee))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    CostRow(title = "Taxes & GST (18%)", amount = formatInr(gst))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = FMWBorder.copy(alpha = 0.6f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Total Payable",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = FMWTextPrimary
                                        )
                                        Text(
                                            text = formatInr(totalAmount),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = FMWOrange
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Payment mode selector
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = FMWSuccessSoft,
                                border = BorderStroke(1.dp, FMWSuccess.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Verified,
                                        contentDescription = null,
                                        tint = FMWSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Pay conveniently via Cash / UPI directly after job completion.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = FMWTextPrimary
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
private fun CostRow(title: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = FMWTextSecondary
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = FMWTextPrimary
        )
    }
}

@Composable
private fun BookingStepProgress(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepNode(stepNumber = 1, label = "Service", isActive = currentStep >= 1, isCompleted = currentStep > 1)
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp),
            color = if (currentStep >= 2) FMWOrange else FMWBorder,
            thickness = 2.dp
        )
        StepNode(stepNumber = 2, label = "Date & Time", isActive = currentStep >= 2, isCompleted = currentStep > 2)
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp),
            color = if (currentStep >= 3) FMWOrange else FMWBorder,
            thickness = 2.dp
        )
        StepNode(stepNumber = 3, label = "Confirm", isActive = currentStep == 3, isCompleted = false)
    }
}

@Composable
private fun StepNode(stepNumber: Int, label: String, isActive: Boolean, isCompleted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (isActive) FMWOrange else FMWSurfaceSubtle)
                .border(1.dp, if (isActive) FMWOrange else FMWBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = "$stepNumber",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.White else FMWTextSecondary
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) FMWNavy else FMWMutedText
        )
    }
}
