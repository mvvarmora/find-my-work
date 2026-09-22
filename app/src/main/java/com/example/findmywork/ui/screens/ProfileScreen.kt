package com.example.findmywork.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Verified
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.model.WorkerProfile
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.theme.FMWAmber
import com.example.findmywork.ui.theme.FMWBgApp
import com.example.findmywork.ui.theme.FMWBgCard
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWDanger
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWNavyDeep
import com.example.findmywork.ui.theme.FMWNavyGradientEnd
import com.example.findmywork.ui.theme.FMWNavyGradientStart
import com.example.findmywork.ui.theme.FMWOrangeCTA
import com.example.findmywork.ui.theme.FMWPrimaryLight
import com.example.findmywork.ui.theme.FMWSpacing
import com.example.findmywork.ui.theme.FMWSuccess
import com.example.findmywork.ui.theme.FMWTextMuted
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary

@Composable
fun ProfileScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onNavigateToEarnings: () -> Unit = {},
    onNavigateToPayments: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    var profile by remember { mutableStateOf<WorkerProfile?>(null) }
    val workerDoc by firestoreRepository.getWorkerFlow(workerId.orEmpty()).collectAsState(initial = null)

    LaunchedEffect(workerId) {
        if (!workerId.isNullOrBlank()) {
            try {
                profile = firestoreRepository.getWorkerProfile(workerId)
            } catch (_: Exception) {}
        }
    }

    val name = profile?.name?.ifBlank { workerDoc?.name }?.ifBlank { "Rajesh Sharma" } ?: "Rajesh Sharma"
    val phone = profile?.phone?.ifBlank { workerDoc?.phone }?.ifBlank { "+91 98250 99001" } ?: "+91 98250 99001"
    val skills = profile?.skills?.ifEmpty { workerDoc?.skills }?.ifEmpty { listOf("Plumbing", "Pipe Repair", "Bathroom Fittings") } ?: listOf("Plumbing")
    val profession = skills.firstOrNull() ?: "Service Professional"
    val expYears = profile?.experienceYears ?: workerDoc?.experienceYears ?: 8
    val pricingRate = profile?.pricing?.hourlyRate?.takeIf { it > 0 } ?: workerDoc?.pricing?.takeIf { it > 0 } ?: 450.0
    val radiusKm = profile?.workingRadiusKm?.takeIf { it > 0 } ?: workerDoc?.workingRadiusKm?.takeIf { it > 0 } ?: 15
    val ratingVal = if ((workerDoc?.ratingCount ?: 0) > 0) {
        (workerDoc?.ratingSum ?: 0.0) / (workerDoc?.ratingCount ?: 1)
    } else 4.9
    val totalJobsVal = workerDoc?.totalJobs?.takeIf { it > 0 } ?: 128
    val completionPct = ((workerDoc?.completionRate ?: 0.96f) * 100).toInt().coerceIn(80, 100)
    val totalEarningsVal = workerDoc?.totalEarnings?.takeIf { it > 0 } ?: 52450.0

    Scaffold(
        containerColor = FMWBgApp
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = FMWSpacing.screenHorizontal, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── 1. Provider Profile Header Hero Card ──
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                border = BorderStroke(1.dp, FMWBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(FMWNavyGradientStart, FMWNavyGradientEnd)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = FMWTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Rounded.Verified,
                                        contentDescription = "Verified Professional",
                                        tint = FMWNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Text(
                                    text = "$profession • $expYears yrs exp",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = FMWTextSecondary
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = phone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FMWTextSecondary
                                )
                            }
                        }

                        IconButton(onClick = onEditProfile) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(FMWPrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = FMWNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = FMWBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // 4-item Performance Stat Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileStatItem(title = "Jobs Done", value = "$totalJobsVal", icon = Icons.Rounded.WorkHistory, accentColor = FMWNavy)
                        ProfileStatItem(title = "Rating", value = "%.1f ★".format(ratingVal), icon = Icons.Rounded.Star, accentColor = FMWAmber)
                        ProfileStatItem(title = "Completion", value = "$completionPct%", icon = Icons.Rounded.CheckCircle, accentColor = FMWSuccess)
                        ProfileStatItem(title = "Total Earned", value = formatInr(totalEarningsVal), icon = Icons.Rounded.TrendingUp, accentColor = FMWNavyDeep)
                    }
                }
            }

            // ── 2. Professional Details Card ──
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                border = BorderStroke(1.dp, FMWBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "PROFESSIONAL DETAILS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavy,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Standard Rate", style = MaterialTheme.typography.labelSmall, color = FMWTextSecondary)
                            Text("${formatInr(pricingRate)} / hr", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FMWTextPrimary)
                        }

                        Column {
                            Text("Working Radius", style = MaterialTheme.typography.labelSmall, color = FMWTextSecondary)
                            Text("$radiusKm km", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FMWTextPrimary)
                        }

                        Column {
                            Text("Status", style = MaterialTheme.typography.labelSmall, color = FMWTextSecondary)
                            Text("Verified Pro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FMWSuccess)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Skills & Specializations", style = MaterialTheme.typography.labelSmall, color = FMWTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        skills.take(4).forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FMWPrimaryLight
                            ) {
                                Text(
                                    text = skill,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FMWNavy,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = onEditProfile,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Professional Profile & Pricing")
                    }
                }
            }

            // ── 3. Quick Actions Group ──
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                border = BorderStroke(1.dp, FMWBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ACCOUNT & PAYOUTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavy,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ProviderMenuRow(
                        icon = Icons.Rounded.TrendingUp,
                        title = "Earnings & Payouts",
                        subtitle = "Track daily, weekly statements & balances",
                        onClick = onNavigateToEarnings
                    )

                    ProviderMenuRow(
                        icon = Icons.Rounded.Payment,
                        title = "Payment Methods & UPI",
                        subtitle = "Direct bank account & UPI ID for payouts",
                        onClick = onNavigateToPayments
                    )

                    ProviderMenuRow(
                        icon = Icons.Rounded.Notifications,
                        title = "Notifications & Job Alerts",
                        subtitle = "Customer job alerts and payment notifications",
                        onClick = onNavigateToNotifications
                    )

                    ProviderMenuRow(
                        icon = Icons.Rounded.AdminPanelSettings,
                        title = "Admin & Analytics",
                        subtitle = "Platform performance dashboard",
                        onClick = onNavigateToAdmin
                    )
                }
            }

            // ── 4. App Preferences & Support Group ──
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                border = BorderStroke(1.dp, FMWBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "APP SETTINGS & SUPPORT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavy,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dark Mode Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(FMWPrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                                    contentDescription = null,
                                    tint = FMWNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Dark Theme", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(if (isDarkTheme) "Dark mode enabled" else "Light mode enabled", fontSize = 12.sp, color = FMWTextSecondary)
                            }
                        }

                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = FMWNavy
                            )
                        )
                    }

                    ProviderMenuRow(
                        icon = Icons.Rounded.Settings,
                        title = "App Settings",
                        subtitle = "Language, permissions, account preferences",
                        onClick = onNavigateToSettings
                    )

                    ProviderMenuRow(
                        icon = Icons.Rounded.HelpOutline,
                        title = "Help & Partner Support",
                        subtitle = "Need help with a job or payout? Contact support",
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:1800123456")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }

            // ── 5. Logout Button ──
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, FMWDanger),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FMWDanger)
            ) {
                Icon(imageVector = Icons.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout from Provider Account", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileStatItem(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = FMWTextPrimary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = FMWTextSecondary,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun ProviderMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(FMWPrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FMWNavy,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = FMWTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = FMWTextSecondary
            )
        }

        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = FMWTextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
