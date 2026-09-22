package com.example.findmywork.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.StatusChip
import com.example.findmywork.ui.components.TimelineIndicator
import com.example.findmywork.ui.components.TimelineStep
import com.example.findmywork.ui.theme.FMWBgApp
import com.example.findmywork.ui.theme.FMWBgCard
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWOrangeCTA
import com.example.findmywork.ui.theme.FMWPrimaryLight
import com.example.findmywork.ui.theme.FMWSuccess
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveJobScreen(
    workerId: String?,
    jobId: String,
    firestoreRepository: FirestoreRepository,
    onNavigateToHome: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val job by firestoreRepository.getJobByIdFlow(jobId).collectAsState(initial = null)

    val statusOrder = listOf("ACCEPTED", "ON_THE_WAY", "ARRIVED", "STARTED", "COMPLETED", "RATED")
    val currentStatus = job?.status ?: "ACCEPTED"
    val currentIdx = statusOrder.indexOf(currentStatus).coerceAtLeast(0)

    val steps = listOf(
        TimelineStep("Job Accepted", currentIdx >= statusOrder.indexOf("ACCEPTED"), active = currentStatus == "ACCEPTED"),
        TimelineStep("On The Way", currentIdx >= statusOrder.indexOf("ON_THE_WAY"), active = currentStatus == "ON_THE_WAY"),
        TimelineStep("Arrived at Location", currentIdx >= statusOrder.indexOf("ARRIVED"), active = currentStatus == "ARRIVED"),
        TimelineStep("Work Started", currentIdx >= statusOrder.indexOf("STARTED"), active = currentStatus == "STARTED"),
        TimelineStep("Completed", currentIdx >= statusOrder.indexOf("COMPLETED"), active = currentStatus == "COMPLETED")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Job Execution Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = FMWTextPrimary
                        )
                    }
                },
                actions = {
                    StatusChip(status = currentStatus)
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FMWBgCard)
            )
        },
        containerColor = FMWBgApp
    ) { innerPadding ->
        if (job == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FMWNavy)
            }
        } else {
            val j = job!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 1. Customer & Job Summary Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FMWBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(FMWPrimaryLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Person,
                                        contentDescription = null,
                                        tint = FMWNavy,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = j.customerName.ifBlank { "Customer" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FMWTextPrimary
                                    )
                                    Text(
                                        text = j.subServiceName.ifBlank { j.categoryName },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = FMWTextSecondary
                                    )
                                }
                            }

                            if (j.customerPhone.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${j.customerPhone}")
                                        }
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(FMWSuccess.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Call,
                                            contentDescription = "Call Customer",
                                            tint = FMWSuccess,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val addressStr = listOfNotNull(
                            j.flatNo.takeIf { it.isNotBlank() },
                            j.societyName.takeIf { it.isNotBlank() },
                            j.landmark.takeIf { it.isNotBlank() },
                            j.city.takeIf { it.isNotBlank() }
                        ).joinToString(", ")

                        if (addressStr.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.LocationOn,
                                    contentDescription = null,
                                    tint = FMWOrangeCTA,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = addressStr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FMWTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Payout Amount",
                                style = MaterialTheme.typography.labelMedium,
                                color = FMWTextSecondary
                            )
                            Text(
                                text = formatInr(j.totalAmount),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = FMWNavy
                            )
                        }
                    }
                }

                // 2. Timeline Progress Indicator
                Text(
                    text = "JOB PROGRESS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWTextSecondary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FMWBorder)
                ) {
                    TimelineIndicator(steps = steps, modifier = Modifier.padding(16.dp))
                }

                // 3. Dynamic Action Button
                val nextAction = when (currentStatus) {
                    "ACCEPTED" -> "ON THE WAY"
                    "ON_THE_WAY" -> "ARRIVED"
                    "ARRIVED" -> "START WORK"
                    "STARTED" -> "COMPLETE JOB"
                    else -> null
                }

                if (nextAction != null) {
                    Button(
                        onClick = {
                            scope.launch {
                                val next = when (currentStatus) {
                                    "ACCEPTED" -> "ON_THE_WAY"
                                    "ON_THE_WAY" -> "ARRIVED"
                                    "ARRIVED" -> "STARTED"
                                    "STARTED" -> "COMPLETED"
                                    else -> currentStatus
                                }
                                firestoreRepository.updateJobStatus(jobId, next)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (nextAction == "COMPLETE JOB") FMWSuccess else FMWNavy
                        )
                    ) {
                        Text(nextAction, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                // 4. Completed State Card
                if (currentStatus == "COMPLETED" || currentStatus == "RATED") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FMWSuccess.copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FMWSuccess.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = FMWSuccess,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Job Completed!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Great job! ${formatInr(j.totalAmount)} has been credited to your earnings.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FMWTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onNavigateToHome,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FMWNavy)
                            ) {
                                Text("Back to Dashboard", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
