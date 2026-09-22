package com.example.findmywork.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.StatusChip
import com.example.findmywork.ui.components.TimelineIndicator
import com.example.findmywork.ui.components.TimelineStep
import com.example.findmywork.ui.theme.FMWBackground
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWNavyDeep
import com.example.findmywork.ui.theme.FMWOrange
import com.example.findmywork.ui.theme.FMWPrimaryLight
import com.example.findmywork.ui.theme.FMWShadow
import com.example.findmywork.ui.theme.FMWSoftBlue
import com.example.findmywork.ui.theme.FMWSuccess
import com.example.findmywork.ui.theme.FMWSurface
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary
import kotlinx.coroutines.launch

@Composable
fun ActiveJobScreen(
    workerId: String?,
    jobId: String,
    firestoreRepository: FirestoreRepository,
    onNavigateToHome: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val job by firestoreRepository.getJobByIdFlow(jobId)
        .collectAsState(initial = null)

    val statusOrder = listOf("ACCEPTED", "ON_THE_WAY", "ARRIVED", "STARTED", "COMPLETED", "RATED")
    val currentStatus = job?.status ?: "ACCEPTED"
    val currentIdx = statusOrder.indexOf(currentStatus).coerceAtLeast(0)

    val steps = listOf(
        TimelineStep("Accepted", currentIdx >= statusOrder.indexOf("ACCEPTED"), active = currentStatus == "ACCEPTED"),
        TimelineStep("On The Way", currentIdx >= statusOrder.indexOf("ON_THE_WAY"), active = currentStatus == "ON_THE_WAY"),
        TimelineStep("Arrived", currentIdx >= statusOrder.indexOf("ARRIVED"), active = currentStatus == "ARRIVED"),
        TimelineStep("Work Started", currentIdx >= statusOrder.indexOf("STARTED"), active = currentStatus == "STARTED"),
        TimelineStep("Completed", currentIdx >= statusOrder.indexOf("COMPLETED"), active = currentStatus == "COMPLETED")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FMWBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        if (job == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FMWNavy)
            }
        } else {
            val j = job!!
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Active Job",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = FMWTextPrimary
                    )
                    Text(
                        text = "Follow the steps to fulfill customer request",
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWTextSecondary
                    )
                }
                StatusChip(status = currentStatus)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Customer Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(18.dp), spotColor = FMWShadow),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FMWSurface),
                border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            val initials = j.customerName
                                .split(" ")
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.uppercase() }
                                .joinToString("")
                                .ifEmpty { "C" }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(FMWNavy.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = FMWNavy
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = j.customerName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FMWTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(FMWSoftBlue.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = j.subServiceName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FMWNavy
                                    )
                                }
                            }
                        }

                        // Call Action Button
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(FMWSuccess.copy(alpha = 0.15f))
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                                    context.startActivity(intent)
                                },
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

                    Spacer(modifier = Modifier.height(14.dp))

                    val addressStr = listOfNotNull(
                        j.flatNo.takeIf { it.isNotBlank() },
                        j.societyName.takeIf { it.isNotBlank() },
                        j.landmark.takeIf { it.isNotBlank() },
                        j.city.takeIf { it.isNotBlank() }
                    ).joinToString(", ")

                    if (addressStr.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(FMWBackground)
                                .border(1.dp, FMWBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.LocationOn,
                                    contentDescription = null,
                                    tint = FMWOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = addressStr,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FMWTextPrimary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(FMWPrimaryLight.copy(alpha = 0.12f))
                                        .clickable {
                                            val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(addressStr))
                                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                            mapIntent.setPackage("com.google.android.apps.maps")
                                            context.startActivity(mapIntent)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Navigation,
                                            contentDescription = "Navigate",
                                            tint = FMWPrimaryLight,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Maps",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FMWPrimaryLight
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Service Total",
                                style = MaterialTheme.typography.labelSmall,
                                color = FMWTextSecondary
                            )
                            Text(
                                text = formatInr(j.totalAmount),
                                style = MaterialTheme.typography.headlineSmall,
                                color = FMWNavyDeep,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "Cash / UPI at completion",
                            style = MaterialTheme.typography.labelSmall,
                            color = FMWTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Job Stepper & Timeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = FMWShadow),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FMWSurface),
                border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.6f))
            ) {
                TimelineIndicator(steps = steps, modifier = Modifier.padding(18.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            val nextAction = when (currentStatus) {
                "ACCEPTED" -> "Mark As 'On The Way'"
                "ON_THE_WAY" -> "Mark As 'Arrived'"
                "ARRIVED" -> "Start Work"
                "STARTED" -> "Complete Job & Collect Payment"
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
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FMWOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(nextAction, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (currentStatus == "COMPLETED" || currentStatus == "RATED") {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = FMWShadow),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(FMWNavyDeep, FMWNavy)
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(FMWSuccess.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = FMWSuccess,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Job Completed Successfully!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Payment of ${formatInr(j.totalAmount)} has been credited to your earnings.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = onNavigateToHome,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FMWOrange,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Back to Dashboard", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
