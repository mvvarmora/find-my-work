package com.example.findmywork.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.SampleMarketplaceData
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.model.Job
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.theme.FMWBgApp
import com.example.findmywork.ui.theme.FMWBgCard
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWOrangeCTA
import com.example.findmywork.ui.theme.FMWPrimaryLight
import com.example.findmywork.ui.theme.FMWSpacing
import com.example.findmywork.ui.theme.FMWTextMuted
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary
import kotlinx.coroutines.launch

@Composable
fun AvailableJobsScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onNavigateToJobDetails: (String) -> Unit,
    onNavigateToActiveJob: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val rawJobs by firestoreRepository.getAvailableJobsFlow().collectAsState(initial = emptyList())

    // If Firestore has no pending jobs, fallback to sample pending jobs
    val jobs = remember(rawJobs) {
        if (rawJobs.isNotEmpty()) {
            rawJobs
        } else {
            SampleMarketplaceData.sampleBookings.filter { it.status == "PENDING" || it.status == "POSTED" }.ifEmpty {
                listOf(
                    Job(
                        id = "JOB-101",
                        customerName = "Priya Singh",
                        customerPhone = "+91 98250 12345",
                        categoryName = "Plumbing",
                        subServiceName = "Kitchen sink leak repair",
                        specialInstructions = "Drain pipe dripping under sink. Needs pipe replacement.",
                        totalAmount = 600.0,
                        distance = 2.4,
                        flatNo = "Flat 402",
                        societyName = "Royal Palms",
                        city = "Rajkot",
                        status = "PENDING"
                    ),
                    Job(
                        id = "JOB-102",
                        customerName = "Rahul Patel",
                        customerPhone = "+91 98250 67890",
                        categoryName = "Electrical",
                        subServiceName = "MCB Tripping & Switchboard",
                        specialInstructions = "Master bedroom circuit tripping repeatedly when AC turned on.",
                        totalAmount = 500.0,
                        distance = 1.8,
                        flatNo = "B-201",
                        societyName = "Kalawad Road",
                        city = "Rajkot",
                        status = "PENDING"
                    ),
                    Job(
                        id = "JOB-103",
                        customerName = "Amit Trivedi",
                        customerPhone = "+91 98250 54321",
                        categoryName = "Carpentry",
                        subServiceName = "Wardrobe Door & Hinge Alignment",
                        specialInstructions = "Heavy sliding wardrobe door stuck off track.",
                        totalAmount = 450.0,
                        distance = 3.1,
                        flatNo = "C-104",
                        societyName = "Shivalik Heights",
                        city = "Rajkot",
                        status = "PENDING"
                    )
                )
            }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FMWBgApp)
                    .padding(horizontal = FMWSpacing.screenHorizontal, vertical = 16.dp)
            ) {
                Text(
                    text = "Available Jobs",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWTextPrimary
                )
                Text(
                    text = "Customer requests in your service radius",
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary
                )
            }
        },
        containerColor = FMWBgApp
    ) { innerPadding ->
        if (jobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                    border = BorderStroke(1.dp, FMWBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Assignment,
                            contentDescription = null,
                            tint = FMWTextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No jobs available right now",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Stay online and we'll notify you as soon as new customers request service in your area.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FMWTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = FMWSpacing.screenHorizontal, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(jobs, key = { it.id }) { job ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                        border = BorderStroke(1.dp, FMWBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(FMWPrimaryLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val icon = when {
                                            job.categoryName.contains("Plumb", ignoreCase = true) -> Icons.Default.Plumbing
                                            job.categoryName.contains("Elect", ignoreCase = true) -> Icons.Default.ElectricBolt
                                            job.categoryName.contains("Clean", ignoreCase = true) -> Icons.Default.CleaningServices
                                            job.categoryName.contains("Carp", ignoreCase = true) -> Icons.Default.Handyman
                                            else -> Icons.Default.Build
                                        }
                                        Icon(icon, contentDescription = null, tint = FMWNavy, modifier = Modifier.size(24.dp))
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = job.categoryName.ifBlank { "Service" },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = FMWNavy
                                        )
                                        Text(
                                            text = job.subServiceName.ifBlank { "General Repair" },
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = FMWTextPrimary
                                        )
                                    }
                                }

                                Text(
                                    text = formatInr(job.totalAmount),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = FMWNavy
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Customer info
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = FMWTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Customer: ${job.customerName.ifBlank { "Homeowner" }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = FMWTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Instructions / Problem description
                            if (job.specialInstructions.isNotBlank()) {
                                Text(
                                    text = job.specialInstructions,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = FMWTextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Distance & Address
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = FMWOrangeCTA,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                val locStr = listOfNotNull(
                                    if (job.distance > 0) "${job.distance} km away" else null,
                                    job.flatNo.takeIf { it.isNotBlank() },
                                    job.societyName.takeIf { it.isNotBlank() },
                                    job.city.takeIf { it.isNotBlank() }
                                ).joinToString(" • ")
                                Text(
                                    text = locStr.ifBlank { "Local area" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FMWTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onNavigateToJobDetails(job.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("VIEW DETAILS")
                                }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            workerId?.let { wid ->
                                                firestoreRepository.acceptJob(job.id, wid)
                                            }
                                            onNavigateToActiveJob(job.id)
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FMWNavy),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("ACCEPT JOB", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
