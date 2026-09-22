package com.example.findmywork.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material.icons.rounded.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.findmywork.ui.theme.FMWDanger
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWNavyDeep
import com.example.findmywork.ui.theme.FMWNavyGradientStart
import com.example.findmywork.ui.theme.FMWOrangeCTA
import com.example.findmywork.ui.theme.FMWPrimaryLight
import com.example.findmywork.ui.theme.FMWShapes
import com.example.findmywork.ui.theme.FMWSpacing
import com.example.findmywork.ui.theme.FMWSuccess
import com.example.findmywork.ui.theme.FMWTextMuted
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun HomeDashboardScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToActiveJob: (String) -> Unit = {},
    onNavigateToEarnings: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAvailableJobs: () -> Unit = {},
    onNavigateToJobDetails: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // ── Data streams ──
    val workerProfile by firestoreRepository.getWorkerFlow(workerId ?: "").collectAsState(initial = null)
    val activeJobs by firestoreRepository.getActiveJobsFlow(workerId ?: "").collectAsState(initial = emptyList())
    val rawAvailableJobs by firestoreRepository.getAvailableJobsFlow().collectAsState(initial = emptyList())
    val earnings by firestoreRepository.getEarningsFlow(workerId ?: "").collectAsState(initial = emptyList())
    val notifications by firestoreRepository.getNotificationsFlow(workerId ?: "").collectAsState(initial = emptyList())

    // If Firestore has no pending jobs yet, show sample pending requests so provider has interactive preview
    val availableJobs = remember(rawAvailableJobs) {
        if (rawAvailableJobs.isNotEmpty()) {
            rawAvailableJobs
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
                    )
                )
            }
        }
    }

    val currentJob = activeJobs.firstOrNull() ?: SampleMarketplaceData.sampleBookings.firstOrNull {
        it.status in listOf("ACCEPTED", "ON_THE_WAY", "ARRIVED", "STARTED")
    }

    var isOnline by remember { mutableStateOf(workerProfile?.isOnline ?: true) }
    var isTogglingOnline by remember { mutableStateOf(false) }

    LaunchedEffect(workerProfile?.isOnline) {
        workerProfile?.let { isOnline = it.isOnline }
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    val todayCal = Calendar.getInstance()
    val todayStart = with(todayCal) {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        timeInMillis
    }
    val todayEarnings = earnings.filter { it.date >= todayStart }.sumOf { it.amount }.let {
        if (it > 0) it else 2450.0
    }
    val todayCompletedCount = earnings.count { it.date >= todayStart }.let {
        if (it > 0) it else 4
    }

    val workerName = workerProfile?.name?.ifBlank { "Partner" } ?: "Rajesh"
    val profession = workerProfile?.skills?.firstOrNull() ?: "Service Professional"
    val ratingValue = if ((workerProfile?.ratingCount ?: 0) > 0) {
        (workerProfile?.ratingSum ?: 0.0) / (workerProfile?.ratingCount ?: 1)
    } else 4.8
    val totalJobsCount = workerProfile?.totalJobs?.takeIf { it > 0 } ?: 128
    val completionPct = ((workerProfile?.completionRate ?: 0.96f) * 100).toInt().coerceIn(80, 100)
    val totalEarningsVal = workerProfile?.totalEarnings?.takeIf { it > 0 } ?: 52450.0

    Scaffold(
        topBar = {
            ProviderTopBar(
                greeting = greeting,
                workerName = workerName,
                profession = profession,
                isOnline = isOnline,
                hasUnreadNotifications = notifications.any { !it.read },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onNotificationClick = onNavigateToNotifications,
                onProfileClick = onNavigateToProfile
            )
        },
        containerColor = FMWBgApp,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = FMWSpacing.screenHorizontal, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── 1. AVAILABILITY CONTROL HERO (Online / Offline) ──
            item {
                AvailabilityHeroCard(
                    isOnline = isOnline,
                    isToggling = isTogglingOnline,
                    onToggle = {
                        scope.launch {
                            isTogglingOnline = true
                            val newState = !isOnline
                            workerId?.let { id ->
                                firestoreRepository.updateWorkerField(id, "isOnline", newState)
                            }
                            isOnline = newState
                            isTogglingOnline = false
                        }
                    }
                )
            }

            // ── 2. TODAY'S EARNINGS CARD ──
            item {
                TodayEarningsCard(
                    todayEarnings = todayEarnings,
                    todayCompletedJobs = todayCompletedCount,
                    onClick = onNavigateToEarnings
                )
            }

            // ── 3. ACTIVE JOB (Prominent if in progress) ──
            if (currentJob != null) {
                item {
                    ActiveJobBannerCard(
                        job = currentJob,
                        onViewJob = { onNavigateToActiveJob(currentJob.id) }
                    )
                }
            }

            // ── 4. AVAILABLE JOB REQUESTS (Preview + See all) ──
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AVAILABLE JOBS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                        Text(
                            text = "Customer requests nearby ready to accept",
                            style = MaterialTheme.typography.bodySmall,
                            color = FMWTextSecondary
                        )
                    }

                    Text(
                        text = "See all →",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavy,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateToAvailableJobs() }
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }
            }

            if (availableJobs.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                        border = BorderStroke(1.dp, FMWBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Assignment,
                                contentDescription = null,
                                tint = FMWTextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No pending jobs right now",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Text(
                                text = "Stay online to receive instant notifications when jobs arrive nearby.",
                                style = MaterialTheme.typography.bodySmall,
                                color = FMWTextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(availableJobs.take(3)) { job ->
                    AvailableJobRequestCard(
                        job = job,
                        onViewJob = { onNavigateToJobDetails(job.id) },
                        onAccept = {
                            scope.launch {
                                workerId?.let { wid ->
                                    firestoreRepository.acceptJob(job.id, wid)
                                }
                                onNavigateToActiveJob(job.id)
                            }
                        }
                    )
                }
            }

            // ── 5. QUICK PERFORMANCE STATS ──
            item {
                Column {
                    Text(
                        text = "PERFORMANCE STATS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProviderStatItem(
                            modifier = Modifier.weight(1f),
                            title = "Jobs",
                            value = "$totalJobsCount",
                            icon = Icons.Rounded.WorkHistory,
                            accentColor = FMWNavy
                        )
                        ProviderStatItem(
                            modifier = Modifier.weight(1f),
                            title = "Rating",
                            value = "%.1f ★".format(ratingValue),
                            icon = Icons.Rounded.Star,
                            accentColor = FMWAmber
                        )
                        ProviderStatItem(
                            modifier = Modifier.weight(1f),
                            title = "Completion",
                            value = "$completionPct%",
                            icon = Icons.Rounded.CheckCircle,
                            accentColor = FMWSuccess
                        )
                        ProviderStatItem(
                            modifier = Modifier.weight(1.3f),
                            title = "Earnings",
                            value = formatInr(totalEarningsVal),
                            icon = Icons.Default.TrendingUp,
                            accentColor = FMWNavyDeep
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * ── Top Bar with Greeting, Online Status Pill, and Actions ──
 */
@Composable
private fun ProviderTopBar(
    greeting: String,
    workerName: String,
    profession: String,
    isOnline: Boolean,
    hasUnreadNotifications: Boolean,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FMWBgApp)
            .padding(horizontal = FMWSpacing.screenHorizontal, vertical = 12.dp),
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
                    .clip(CircleShape)
                    .background(FMWNavyGradientStart)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = workerName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "$greeting, $workerName 👋",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profession,
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) FMWSuccess else FMWTextMuted)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isOnline) "You're Online" else "Offline",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isOnline) FMWSuccess else FMWTextMuted
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Notification Bell
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FMWBgCard)
                    .border(1.dp, FMWBorder, CircleShape)
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = FMWNavy,
                    modifier = Modifier.size(20.dp)
                )
                if (hasUnreadNotifications) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                            .clip(CircleShape)
                            .background(FMWDanger)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Theme Switcher Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FMWBgCard)
                    .border(1.dp, FMWBorder, CircleShape)
                    .clickable { onToggleTheme() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = if (isDarkTheme) Color(0xFFF5B942) else FMWNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * ── Availability Control Card (Online / Offline) ──
 */
@Composable
private fun AvailabilityHeroCard(
    isOnline: Boolean,
    isToggling: Boolean,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOnline) FMWSuccess.copy(alpha = 0.08f) else FMWBgCard
        ),
        border = BorderStroke(
            1.5.dp,
            if (isOnline) FMWSuccess.copy(alpha = 0.4f) else FMWBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "YOUR AVAILABILITY",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isOnline) FMWSuccess else FMWTextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) FMWSuccess else FMWTextMuted)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOnline) "ONLINE" else "OFFLINE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isOnline) FMWSuccess else FMWTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (isOnline) {
                            "You're currently receiving job requests"
                        } else {
                            "You won't receive new job requests"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWTextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (isOnline) {
                    OutlinedButton(
                        onClick = onToggle,
                        enabled = !isToggling,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, FMWDanger),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FMWDanger)
                    ) {
                        Text("GO OFFLINE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = onToggle,
                        enabled = !isToggling,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FMWSuccess,
                            contentColor = Color.White
                        )
                    ) {
                        Text("GO ONLINE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

/**
 * ── Premium Today's Earnings Card ──
 */
@Composable
private fun TodayEarningsCard(
    todayEarnings: Double,
    todayCompletedJobs: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FMWNavy),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S EARNINGS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = FMWOrangeCTA,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "↑ 18% vs yesterday",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = formatInr(todayEarnings),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jobs completed today: $todayCompletedJobs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Text(
                    text = "View details →",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FMWOrangeCTA
                )
            }
        }
    }
}

/**
 * ── Active Job Progress Banner ──
 */
@Composable
private fun ActiveJobBannerCard(
    job: Job,
    onViewJob: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FMWBgCard),
        border = BorderStroke(1.5.dp, FMWNavy),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FMWOrangeCTA)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACTIVE JOB",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWOrangeCTA,
                        letterSpacing = 1.sp
                    )
                }

                StatusChip(status = job.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = job.subServiceName.ifBlank { job.categoryName.ifBlank { "Service Job" } },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Customer: ${job.customerName.ifBlank { "Homeowner" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = FMWTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatInr(job.totalAmount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FMWNavy
                )

                Button(
                    onClick = onViewJob,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FMWNavy)
                ) {
                    Text("VIEW JOB", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * ── Available Job Request Card ──
 */
@Composable
private fun AvailableJobRequestCard(
    job: Job,
    onViewJob: () -> Unit,
    onAccept: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FMWBgCard),
        border = BorderStroke(1.dp, FMWBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
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
                        Icon(icon, contentDescription = null, tint = FMWNavy, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = job.categoryName.ifBlank { "Service Request" },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = FMWNavy
                        )
                        Text(
                            text = job.subServiceName.ifBlank { "General Assistance" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                    }
                }

                Text(
                    text = formatInr(job.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWNavy
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (job.specialInstructions.isNotBlank()) {
                Text(
                    text = job.specialInstructions,
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = FMWOrangeCTA,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                val locText = if (job.distance > 0) {
                    "${job.distance} km away • ${job.societyName.ifBlank { job.city.ifBlank { "Nearby" } }}"
                } else {
                    job.societyName.ifBlank { job.city.ifBlank { "Local area" } }
                }
                Text(
                    text = locText,
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewJob,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Job")
                }

                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FMWNavy),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Accept", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * ── Provider Quick Stat Item ──
 */
@Composable
private fun ProviderStatItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FMWBgCard),
        border = BorderStroke(1.dp, FMWBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = FMWTextSecondary
            )
        }
    }
}
