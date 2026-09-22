package com.example.findmywork.ui.screens

import com.example.findmywork.ui.theme.*
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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Engineering
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.SampleMarketplaceData
import com.example.findmywork.data.formatInr
import com.example.findmywork.ui.theme.FMWAmber
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWPrimaryLight
import com.example.findmywork.ui.theme.FMWSuccess
import com.example.findmywork.ui.theme.FMWTextMuted
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Admin & Platform Analytics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                        Text(
                            text = "Live Marketplace Operations",
                            style = MaterialTheme.typography.labelSmall,
                            color = FMWTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = FMWTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FMWBgCard)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FMWBgApp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. KPI Grid (2x2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "Total Bookings",
                    value = "142",
                    delta = "+18% this wk",
                    isPositive = true,
                    icon = Icons.Rounded.Engineering,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Active Craftsmen",
                    value = "28",
                    delta = "All verified",
                    isPositive = true,
                    icon = Icons.Rounded.People,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "Marketplace GMV",
                    value = "₹1,84,500",
                    delta = "+24% MoM",
                    isPositive = true,
                    icon = Icons.Rounded.MonetizationOn,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Customer CSAT",
                    value = "4.8 ★",
                    delta = "99.2% positive",
                    isPositive = true,
                    icon = Icons.Rounded.Star,
                    modifier = Modifier.weight(1f)
                )
            }

            // 2. Pending Verification Queue
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pending Craftsmen Verification",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(FMWAmberSoft)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "2 Pending",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FMWAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    VerificationItem(
                        name = "Rajesh Sharma",
                        trade = "Senior Electrician",
                        experience = "7 years • Wireman License",
                        onApprove = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Rajesh Sharma approved & verified!")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = FMWBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    VerificationItem(
                        name = "Manish Patel",
                        trade = "Plumbing Specialist",
                        experience = "4 years • Govt ITI Certified",
                        onApprove = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Manish Patel approved & verified!")
                            }
                        }
                    )
                }
            }

            // 3. Live System Health
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FMWBgCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "System Infrastructure Status",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HealthRow("Firestore Realtime Database", "Operational", FMWSuccess)
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthRow("Firebase Auth Service", "Healthy", FMWSuccess)
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthRow("Direct UPI Payment Gateway", "Online", FMWSuccess)
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthRow("Push Notifications & Dispatch", "Optimal", FMWSuccess)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    delta: String,
    isPositive: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FMWBgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, color = FMWTextSecondary)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FMWPrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = FMWNavy, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FMWTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = delta,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPositive) FMWSuccess else FMWDanger
            )
        }
    }
}

@Composable
private fun VerificationItem(
    name: String,
    trade: String,
    experience: String,
    onApprove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(FMWPrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Text(text = name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = FMWNavy)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FMWTextPrimary)
            Text(text = trade, fontSize = 12.sp, color = FMWTextSecondary)
            Text(text = experience, fontSize = 11.sp, color = FMWTextMuted)
        }

        Button(
            onClick = onApprove,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FMWNavy),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Verify", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HealthRow(service: String, status: String, statusColor: Color) {
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
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = service, fontSize = 12.sp, color = FMWTextSecondary)
        }
        Text(text = status, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = statusColor)
    }
}
