package com.example.findmywork.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material.icons.rounded.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.StatCard
import com.example.findmywork.ui.components.StatusChip
import com.example.findmywork.ui.theme.FMWAmber
import com.example.findmywork.ui.theme.FMWAmberSoft
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
import java.util.Calendar

@Composable
fun HomeDashboardScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToActiveJob: (String) -> Unit = {},
    onNavigateToEarnings: () -> Unit = {},
    onNavigateToJobs: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onSwitchToCustomer: () -> Unit = {}
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
            .background(FMWBackground)
    ) {
        // Top App Bar
        TopAppBar(
            workerName = worker?.name?.ifBlank { "Rajesh Varmora" } ?: "Rajesh Varmora",
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
            Spacer(modifier = Modifier.height(2.dp))

            // Quick Switch to Customer Marketplace Banner
            ModeSwitchBanner(onSwitchToCustomer = onSwitchToCustomer)

            // Today's Earnings Hero Card (Navy Gradient)
            TodaysEarningsCard(
                earnings = todayEarnings,
                onNavigateToEarnings = onNavigateToEarnings
            )

            // Available Jobs Spotlight Card
            AvailableJobsBanner(onNavigateToJobs = onNavigateToJobs)

            // Active / Current Job Card
            if (currentJob != null) {
                CurrentJobSection(
                    job = currentJob,
                    onNavigateToActiveJob = { onNavigateToActiveJob(currentJob.id) }
                )
            }

            // Quick Stats Horizontal Grid
            val avgRating = if ((worker?.ratingCount ?: 0) > 0)
                (worker!!.ratingSum / worker!!.ratingCount).toFloat() else 4.9f
            val displayJobs = if ((worker?.totalJobs ?: 0) > 0) worker!!.totalJobs else 142
            val displayEarnings: Double = worker?.totalEarnings?.takeIf { it > 0.0 } ?: 28450.0

            QuickStats(
                totalJobs = displayJobs,
                rating = avgRating,
                completionRate = worker?.completionRate ?: 0.98f,
                totalEarnings = displayEarnings,
                onNavigateToJobs = onNavigateToJobs,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToEarnings = onNavigateToEarnings
            )

            // Recent Notifications
            if (notifications.isNotEmpty()) {
                RecentNotificationsSection(
                    notifications = notifications.take(3),
                    onNavigateToNotifications = onNavigateToNotifications
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = FMWSurface,
        border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.5f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Hello, $workerName",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Rounded.Verified,
                        contentDescription = "Verified Provider",
                        tint = FMWPrimaryLight,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Pulsing Online/Offline status badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isOnline) FMWSuccess.copy(alpha = 0.12f)
                            else FMWTextSecondary.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    val transition = rememberInfiniteTransition(label = "pulse")
                    val pulseAlpha by transition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dotAlpha"
                    )

                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .alpha(if (isOnline) pulseAlpha else 0.5f)
                            .clip(CircleShape)
                            .background(if (isOnline) FMWSuccess else FMWTextSecondary)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isOnline) "Online · Ready for jobs" else "Offline",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isOnline) FMWSuccess else FMWTextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isOnline,
                    onCheckedChange = { if (!(hasActiveJob && isOnline)) onToggleOnline() },
                    enabled = !(isTogglingOnline || (hasActiveJob && isOnline)),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = FMWSuccess,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = FMWBorder
                    )
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(FMWBackground)
                        .border(1.dp, FMWBorder, CircleShape)
                        .clickable { onNavigateToNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = "Notifications",
                        tint = FMWNavy,
                        modifier = Modifier.size(22.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FMWOrange)
                            .align(Alignment.TopEnd)
                            .padding(top = 2.dp, end = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeSwitchBanner(onSwitchToCustomer: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSwitchToCustomer() }
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = FMWShadow),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FMWNavyDeep)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SwapHoriz,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Worker Partner Mode",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Need a service? Switch to Customer View",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(FMWOrange)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Customer Mode",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TodaysEarningsCard(earnings: Double, onNavigateToEarnings: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = FMWShadow)
            .clickable { onNavigateToEarnings() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(FMWNavyDeep, FMWNavy, FMWPrimaryLight)
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Payments,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Today's Earnings",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "View details",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = formatInr(earnings),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (earnings > 0) "${formatInr(earnings)} earned today" else "Accept available jobs to start earning",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AvailableJobsBanner(onNavigateToJobs: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = FMWShadow)
            .clickable { onNavigateToJobs() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FMWSurface),
        border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FMWOrange.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Work,
                        contentDescription = null,
                        tint = FMWOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Available Jobs",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(FMWOrange)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "HOT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tap to view and accept nearby jobs",
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWTextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(FMWOrange.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "Go to Jobs",
                    tint = FMWOrange,
                    modifier = Modifier.size(18.dp)
                )
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
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(FMWSuccess)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Current Active Job",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary
            )
        }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val initials = job.customerName
                            .split(" ")
                            .take(2)
                            .mapNotNull { it.firstOrNull()?.uppercase() }
                            .joinToString("")
                            .ifEmpty { "C" }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FMWNavy.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontWeight = FontWeight.Bold,
                                color = FMWNavy,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = job.customerName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Text(
                                text = job.subServiceName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = FMWTextSecondary
                            )
                        }
                    }
                    StatusChip(status = job.status)
                }

                Spacer(modifier = Modifier.height(14.dp))

                val addressStr = listOfNotNull(
                    job.flatNo.takeIf { it.isNotBlank() },
                    job.societyName.takeIf { it.isNotBlank() },
                    job.landmark.takeIf { it.isNotBlank() },
                    job.city.takeIf { it.isNotBlank() }
                ).joinToString(", ")

                if (addressStr.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(FMWBackground)
                            .border(1.dp, FMWBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                tint = FMWOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = addressStr,
                                style = MaterialTheme.typography.bodySmall,
                                color = FMWTextSecondary,
                                maxLines = 2
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onNavigateToActiveJob,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FMWNavy,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "View Job Details",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
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
    totalEarnings: Double,
    onNavigateToJobs: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToEarnings: () -> Unit = {}
) {
    Column {
        Text(
            text = "Performance Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FMWTextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                StatCard(
                    icon = Icons.Rounded.WorkHistory,
                    value = totalJobs.toString(),
                    label = "Total Jobs",
                    iconColor = FMWPrimaryLight,
                    onClick = onNavigateToJobs
                )
            }
            item {
                StatCard(
                    icon = Icons.Rounded.Star,
                    value = "%.1f".format(rating),
                    label = "Rating",
                    iconColor = FMWAmber,
                    onClick = onNavigateToProfile
                )
            }
            item {
                StatCard(
                    icon = Icons.Rounded.CheckCircle,
                    value = "%.0f%%".format(completionRate * 100),
                    label = "Completion",
                    iconColor = FMWSuccess,
                    onClick = onNavigateToHistory
                )
            }
            item {
                StatCard(
                    icon = Icons.Rounded.Payments,
                    value = formatInr(totalEarnings),
                    label = "Lifetime",
                    iconColor = FMWOrange,
                    onClick = onNavigateToEarnings
                )
            }
        }
    }
}

@Composable
private fun RecentNotificationsSection(
    notifications: List<com.example.findmywork.data.model.Notification>,
    onNavigateToNotifications: () -> Unit = {}
) {
    Column(modifier = Modifier.clickable { onNavigateToNotifications() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "See all",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FMWPrimaryLight
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "View all",
                    tint = FMWPrimaryLight,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

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
                    "payment" -> FMWOrange
                    "job_complete" -> FMWSuccess
                    "rating" -> FMWAmber
                    else -> FMWNavy
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FMWSurface),
        border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg.copy(alpha = 0.12f)),
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
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = FMWTextPrimary
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = FMWTextSecondary
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = FMWTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
