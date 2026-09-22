package com.example.findmywork.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.SampleMarketplaceData
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.model.Job
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.StatusChip
import com.example.findmywork.ui.theme.FMWAmber
import com.example.findmywork.ui.theme.FMWBgApp
import com.example.findmywork.ui.theme.FMWBgCard
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWSpacing
import com.example.findmywork.ui.theme.FMWTextMuted
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary

@Composable
fun JobHistoryScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onSelectJob: (String) -> Unit = {}
) {
    val rawJobs by firestoreRepository.getCompletedJobsFlow(workerId ?: "")
        .collectAsState(initial = emptyList())

    val jobs = remember(rawJobs) {
        if (rawJobs.isNotEmpty()) {
            rawJobs
        } else {
            SampleMarketplaceData.sampleBookings.filter { it.status == "COMPLETED" || it.status == "RATED" }.ifEmpty {
                listOf(
                    Job(
                        id = "JOB-COMP-1",
                        customerName = "Priya Singh",
                        categoryName = "AC & Appliance",
                        subServiceName = "AC Repair & Cooling Service",
                        totalAmount = 850.0,
                        rating = 5,
                        review = "Super quick service and excellent cooling now!",
                        status = "COMPLETED",
                        bookingDate = "Today"
                    ),
                    Job(
                        id = "JOB-COMP-2",
                        customerName = "Rahul Patel",
                        categoryName = "Plumbing",
                        subServiceName = "Kitchen Sink Pipe Fix",
                        totalAmount = 600.0,
                        rating = 5,
                        review = "Punctual, polite and clean workmanship.",
                        status = "COMPLETED",
                        bookingDate = "Yesterday"
                    ),
                    Job(
                        id = "JOB-COMP-3",
                        customerName = "Amit Trivedi",
                        categoryName = "Electrical",
                        subServiceName = "Switchboard Wiring Overhaul",
                        totalAmount = 400.0,
                        rating = 4,
                        review = "Solved tripping issue in 20 minutes.",
                        status = "COMPLETED",
                        bookingDate = "15 Sep 2026"
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
                    text = "COMPLETED JOBS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = FMWNavy,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Job History",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWTextPrimary
                )
                Text(
                    text = "All service assignments completed by you",
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
                            imageVector = Icons.Rounded.History,
                            contentDescription = null,
                            tint = FMWTextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No completed jobs yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "When you complete jobs, your job records, earnings, and ratings will appear here.",
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
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                        border = BorderStroke(1.dp, FMWBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectJob(job.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header Row: Service title & Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = job.subServiceName.ifBlank { job.categoryName.ifBlank { "Completed Service" } },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FMWTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Person,
                                            contentDescription = null,
                                            tint = FMWTextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = job.customerName.ifBlank { "Customer" },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FMWTextSecondary
                                        )
                                        if (job.bookingDate.isNotBlank()) {
                                            Text(
                                                text = " • ${job.bookingDate}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = FMWTextSecondary
                                            )
                                        }
                                    }
                                }

                                StatusChip(status = "COMPLETED")
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = FMWBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Bottom Row: Earnings & Rating
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Earned",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FMWTextSecondary
                                    )
                                    Text(
                                        text = formatInr(job.totalAmount),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = FMWNavy
                                    )
                                }

                                if (job.rating > 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Star,
                                            contentDescription = null,
                                            tint = FMWAmber,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "%.1f ★".format(job.rating.toDouble()),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = FMWAmber
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "5.0 ★",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FMWAmber
                                    )
                                }
                            }

                            if (job.review.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "\"${job.review}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FMWTextSecondary,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
