package com.example.findmywork.ui.screens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.findmywork.ui.components.StatusChip
import com.example.findmywork.ui.components.StatCard
import com.example.findmywork.data.repository.FirestoreRepository
import kotlinx.coroutines.launch
import com.example.findmywork.data.formatInr
import java.util.Calendar

@Composable
fun HomeDashboardScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToActiveJob: (String) -> Unit = {},
    onNavigateToEarnings: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val worker by firestoreRepository.getWorkerFlow(workerId ?: "")
        .collectAsState(initial = null)
    val activeJobs by firestoreRepository.getActiveJobsFlow(workerId ?: "")
        .collectAsState(initial = emptyList())
    val notifications by firestoreRepository.getNotificationsFlow(workerId ?: "")
        .collectAsState(initial = emptyList())
    val earnings by firestoreRepository.getEarningsFlow(workerId ?: "")
        .collectAsState(initial = emptyList())

    val currentJob = activeJobs.firstOrNull()
    var isOnline by remember { mutableStateOf(worker?.isOnline ?: false) }
    var isTogglingOnline by remember { mutableStateOf(false) }

    LaunchedEffect(worker?.isOnline) {
        worker?.let { isOnline = it.isOnline }
    }

    val todayCal = Calendar.getInstance()
    val todayStart = with(todayCal) {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        timeInMillis
    }
    val todayEarnings = earnings.filter { it.date >= todayStart }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            workerName = worker?.name ?: "Worker",
            isOnline = isOnline,
            isTogglingOnline = isTogglingOnline,
            onToggleOnline = {
                scope.launch {
                    isTogglingOnline = true
                    val newState = !isOnline
                    firestoreRepository.updateWorkerField(workerId ?: "", "isOnline", newState)
                    isOnline = newState
                    isTogglingOnline = false
                }
            },
            hasActiveJob = currentJob != null,
            onNavigateToNotifications = onNavigateToNotifications
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            TodaysEarningsCard(
                earnings = todayEarnings,
                onNavigateToEarnings = onNavigateToEarnings
            )
            if (currentJob != null) {
                CurrentJobSection(
                    job = currentJob,
                    onNavigateToActiveJob = { onNavigateToActiveJob(currentJob.id) }
                )
            }
            val avgRating = if ((worker?.ratingCount ?: 0) > 0)
                (worker!!.ratingSum / worker!!.ratingCount).toFloat() else 0f
            QuickStats(
                totalJobs = worker?.totalJobs ?: 0,
                rating = avgRating,
                completionRate = worker?.completionRate ?: 0f,
                totalEarnings = worker?.totalEarnings ?: 0.0
            )
            if (notifications.isNotEmpty()) {
                RecentNotificationsSection(notifications = notifications.take(3))
            }
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
private fun TopAppBar(
    workerName: String,
    isOnline: Boolean,
    isTogglingOnline: Boolean,
    onToggleOnline: () -> Unit,
    hasActiveJob: Boolean = false,
    onNavigateToNotifications: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hello, ${workerName}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isOnline) "Online" else "Offline",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = isOnline,
                onCheckedChange = { if (!(hasActiveJob && isOnline)) onToggleOnline() },
                enabled = !(isTogglingOnline || (hasActiveJob && isOnline)),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.secondary,
                    checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.error,
                    uncheckedTrackColor = MaterialTheme.colorScheme.errorContainer
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.clickable { onNavigateToNotifications() }) {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                        .align(Alignment.TopEnd)
                        .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun TodaysEarningsCard(earnings: Double, onNavigateToEarnings: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            Column {
                Text(
                    text = "Today's Earnings",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                )
                Text(
                    text = formatInr(earnings),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (earnings > 0) {
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .border(1.dp, MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
                            .clip(RoundedCornerShape(100.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "earned today",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentJobSection(
    job: com.example.findmywork.data.model.Job,
    onNavigateToActiveJob: () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Current Job",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(job.customerName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            job.subServiceName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusChip(status = job.status)
                }
                Spacer(modifier = Modifier.height(16.dp))

                val addressStr = listOfNotNull(
                    job.flatNo.takeIf { it.isNotBlank() },
                    job.societyName.takeIf { it.isNotBlank() },
                    job.landmark.takeIf { it.isNotBlank() },
                    job.city.takeIf { it.isNotBlank() }
                ).joinToString(", ")
                if (addressStr.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                addressStr,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onNavigateToActiveJob,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("View Details", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun QuickStats(
    totalJobs: Int,
    rating: Float,
    completionRate: Float,
    totalEarnings: Double
) {
    Column {
        Text("Quick Stats", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                StatCard(
                    icon = Icons.Rounded.WorkHistory,
                    value = totalJobs.toString(),
                    label = "Total Jobs",
                    iconColor = MaterialTheme.colorScheme.primaryFixed
                )
            }
            item {
                StatCard(
                    icon = Icons.Rounded.Star,
                    value = "%.1f".format(rating),
                    label = "Rating",
                    iconColor = MaterialTheme.colorScheme.tertiary
                )
            }
            item {
                StatCard(
                    icon = Icons.Rounded.CheckCircle,
                    value = "%.0f%%".format(completionRate * 100),
                    label = "Completion",
                    iconColor = MaterialTheme.colorScheme.secondary
                )
            }
            item {
                StatCard(
                    icon = Icons.Rounded.Payments,
                    value = formatInr(totalEarnings),
                    label = "Earnings",
                    iconColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun RecentNotificationsSection(
    notifications: List<com.example.findmywork.data.model.Notification>
) {
    Column {
        Text(
            "Recent Notifications",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        notifications.forEach { notification ->
            NotificationItem(
                icon = when (notification.type) {
                    "payment" -> Icons.Rounded.Payments
                    "job_complete" -> Icons.Rounded.CheckCircle
                    "rating" -> Icons.Rounded.Star
                    else -> Icons.AutoMirrored.Rounded.Assignment
                },
                text = notification.message,
                time = formatTime(notification.createdAt),
                iconBg = when (notification.type) {
                    "payment" -> MaterialTheme.colorScheme.secondary
                    "job_complete" -> MaterialTheme.colorScheme.secondary
                    "rating" -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun formatTime(timestamp: Long): String {
    if (timestamp <= 0) return ""
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000} mins ago"
        diff < 86_400_000 -> "${diff / 3_600_000} hours ago"
        else -> {
            val sdf = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
            sdf.format(java.util.Date(timestamp))
        }
    }
}

@Composable
private fun NotificationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    time: String,
    iconBg: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconBg,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text, style = MaterialTheme.typography.bodyMedium)
            Text(time, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
