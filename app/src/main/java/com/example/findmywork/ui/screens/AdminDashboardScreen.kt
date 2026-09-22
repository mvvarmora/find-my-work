package com.example.findmywork.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.findmywork.ui.theme.*

@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Surface(
                color = FMWSurface,
                border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = FMWTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Admin Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                        Text(
                            text = "Marketplace Health & Administration",
                            style = MaterialTheme.typography.bodySmall,
                            color = FMWTextSecondary
                        )
                    }
                }
            }
        },
        containerColor = FMWBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Metrics Overview Grid
            Text(
                text = "Marketplace Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard(
                    title = "Active Pros",
                    value = "148",
                    delta = "+12% this mo",
                    icon = Icons.Rounded.Engineering,
                    iconColor = FMWBlue,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Completed Jobs",
                    value = "1,842",
                    delta = "98.4% on-time",
                    icon = Icons.Rounded.CheckCircle,
                    iconColor = FMWSuccess,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard(
                    title = "Customer Base",
                    value = "3,210",
                    delta = "+180 this wk",
                    icon = Icons.Rounded.People,
                    iconColor = FMWNavy,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Avg Rating",
                    value = "4.86 ★",
                    delta = "Based on 1.2k reviews",
                    icon = Icons.Rounded.Star,
                    iconColor = FMWStarYellow,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Management Quick Tools
            Text(
                text = "Administrative Controls",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary
            )

            AdminActionTile(
                title = "Verification Queue",
                subtitle = "3 new worker documents waiting for approval",
                icon = Icons.Rounded.VerifiedUser,
                badgeText = "3 Pending",
                onClick = {}
            )

            AdminActionTile(
                title = "Live Dispatched Jobs",
                subtitle = "8 active appointments in Rajkot currently in progress",
                icon = Icons.Rounded.DirectionsCar,
                badgeText = "8 Live",
                onClick = {}
            )

            AdminActionTile(
                title = "Categories & Tariffs",
                subtitle = "Configure base pricing and service commission rates",
                icon = Icons.Rounded.Category,
                badgeText = null,
                onClick = {}
            )

            AdminActionTile(
                title = "Dispute Resolution Center",
                subtitle = "Review customer cancellation inquiries and claims",
                icon = Icons.Rounded.SupportAgent,
                badgeText = "0 Active",
                onClick = {}
            )
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    delta: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = FMWSurface,
        border = BorderStroke(1.dp, FMWBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FMWTextPrimary)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = FMWTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = delta, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = FMWSuccess)
        }
    }
}

@Composable
private fun AdminActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String?,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = FMWSurface,
        border = BorderStroke(1.dp, FMWBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FMWSoftBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = FMWBlue, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FMWTextPrimary)
                    if (badgeText != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(FMWOrange.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = badgeText, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FMWOrange)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = FMWTextSecondary)
            }
            Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = null, tint = FMWTextSecondary)
        }
    }
}
