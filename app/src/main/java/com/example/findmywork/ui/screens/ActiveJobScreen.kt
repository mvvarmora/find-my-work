package com.example.findmywork.ui.screens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.findmywork.ui.components.StatusChip
import com.example.findmywork.ui.components.TimelineIndicator
import com.example.findmywork.ui.components.TimelineStep
import com.example.findmywork.data.model.JobStatus
import com.example.findmywork.data.repository.FirestoreRepository
import kotlinx.coroutines.launch

@Composable
fun ActiveJobScreen(
    workerId: String?,
    jobId: String,
    firestoreRepository: FirestoreRepository,
    onNavigateToHome: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val job by firestoreRepository.getJobByIdFlow(jobId)
        .collectAsState(initial = null)

    val currentStatus = job?.status ?: JobStatus.ACCEPTED

    val steps = listOf(
        TimelineStep("Accepted", currentStatus >= JobStatus.ACCEPTED, active = currentStatus == JobStatus.ACCEPTED),
        TimelineStep("On The Way", currentStatus >= JobStatus.ON_THE_WAY, active = currentStatus == JobStatus.ON_THE_WAY),
        TimelineStep("Arrived", currentStatus >= JobStatus.ARRIVED, active = currentStatus == JobStatus.ARRIVED),
        TimelineStep("Work Started", currentStatus >= JobStatus.STARTED, active = currentStatus == JobStatus.STARTED),
        TimelineStep("Completed", currentStatus >= JobStatus.COMPLETED, active = currentStatus == JobStatus.COMPLETED)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        if (job == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val j = job!!
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Active Job", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                StatusChip(status = currentStatus)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(j.customerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(j.service, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (j.address.isNotBlank()) {
                        Text(j.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("\$${j.price.toInt()}", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Progress", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                TimelineIndicator(steps = steps, modifier = Modifier.padding(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Actions", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 12.dp))

            val nextAction = when (currentStatus) {
                JobStatus.ACCEPTED -> "On The Way"
                JobStatus.ON_THE_WAY -> "Arrived"
                JobStatus.ARRIVED -> "Start Work"
                JobStatus.STARTED -> "Complete Job"
                else -> null
            }

            if (nextAction != null) {
                Button(
                    onClick = {
                        scope.launch {
                            val next = when (currentStatus) {
                                JobStatus.ACCEPTED -> JobStatus.ON_THE_WAY
                                JobStatus.ON_THE_WAY -> JobStatus.ARRIVED
                                JobStatus.ARRIVED -> JobStatus.STARTED
                                JobStatus.STARTED -> JobStatus.COMPLETED
                                else -> currentStatus
                            }
                            firestoreRepository.updateJobStatus(jobId, next)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary)
                ) {
                    Text(nextAction, fontWeight = FontWeight.SemiBold)
                }
            }

            if (currentStatus == JobStatus.COMPLETED || currentStatus == JobStatus.RATED) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Job Completed!",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Great work! The job has been marked as complete.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToHome,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Back to Home", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
