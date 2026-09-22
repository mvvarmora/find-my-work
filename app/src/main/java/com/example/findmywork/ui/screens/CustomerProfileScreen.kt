package com.example.findmywork.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.findmywork.data.formatInr
import com.example.findmywork.ui.components.marketplace.WorkerAvatar
import com.example.findmywork.ui.theme.*

@Composable
fun CustomerProfileScreen(
    customerName: String = "Ronen Sharma",
    customerEmail: String = "ronen.sharma@example.com",
    walletBalance: Double = 3564.0,
    bookingCount: Int = 12,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onEditProfile: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onSwitchToProvider: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToHelpSupport: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = FMWBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Profile Hero Card
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = FMWSurface,
                border = BorderStroke(1.dp, FMWBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WorkerAvatar(
                        name = customerName,
                        size = 76.dp,
                        isVerified = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = customerName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = customerEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FMWTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Role Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(FMWSoftBlue)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Verified Customer",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = FMWBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = FMWBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Wallet & Bookings Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = formatInr(walletBalance),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWNavy
                            )
                            Text(
                                text = "Wallet Balance",
                                style = MaterialTheme.typography.bodySmall,
                                color = FMWTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(FMWBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$bookingCount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWOrange
                            )
                            Text(
                                text = "Total Bookings",
                                style = MaterialTheme.typography.bodySmall,
                                color = FMWTextSecondary
                            )
                        }
                    }
                }
            }

            // 2. Account Section
            ProfileSectionCard(title = "Account") {
                ProfileOptionRow(
                    icon = Icons.Rounded.PersonOutline,
                    iconTint = FMWBlue,
                    title = "Edit Profile Details",
                    subtitle = "Name, phone, and saved home address",
                    onClick = onEditProfile
                )
                HorizontalDivider(color = FMWBorder.copy(alpha = 0.4f))
                ProfileOptionRow(
                    icon = Icons.Rounded.CalendarMonth,
                    iconTint = FMWNavy,
                    title = "My Bookings & History",
                    subtitle = "Manage appointments and review receipts",
                    onClick = onNavigateToBookings
                )
            }

            // 3. Payments Section
            ProfileSectionCard(title = "Payments") {
                ProfileOptionRow(
                    icon = Icons.Rounded.AccountBalanceWallet,
                    iconTint = FMWOrange,
                    title = "Wallet & Payment Methods",
                    subtitle = "Bank account, UPI, and transaction history",
                    onClick = onNavigateToPayments
                )
            }

            // 4. Provider Mode Section (Prominent Switch Banner)
            Surface(
                onClick = onSwitchToProvider,
                shape = RoundedCornerShape(18.dp),
                color = FMWNavyDeep,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Engineering,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Switch to Provider Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Accept incoming jobs and view earnings",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            // 5. Help & Settings Section
            ProfileSectionCard(title = "Support & Settings") {
                ProfileOptionRow(
                    icon = Icons.Rounded.AdminPanelSettings,
                    iconTint = FMWBlue,
                    title = "Admin Panel",
                    subtitle = "Platform stats, bookings monitor & provider roster",
                    onClick = onNavigateToAdmin
                )
                HorizontalDivider(color = FMWBorder.copy(alpha = 0.4f))
                ProfileOptionRow(
                    icon = Icons.Rounded.HelpOutline,
                    iconTint = FMWNavy,
                    title = "Help & Customer Support",
                    subtitle = "FAQs, live assistance & dispute resolution",
                    onClick = onNavigateToHelpSupport
                )
            }

            // 6. Preferences Section
            ProfileSectionCard(title = "Appearance") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onToggleTheme() }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(FMWSurfaceSubtle),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                                contentDescription = null,
                                tint = if (isDarkTheme) FMWWarning else FMWOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isDarkTheme) "Dark Mode" else "Light Mode",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = FMWTextPrimary
                            )
                            Text(
                                text = if (isDarkTheme) "Dark theme enabled • Tap to switch" else "Light theme enabled • Tap to switch",
                                style = MaterialTheme.typography.labelSmall,
                                color = FMWTextSecondary
                            )
                        }
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = FMWOrange,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = FMWBorder
                        )
                    )
                }
            }

            // 7. Logout Button
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, FMWError.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FMWError),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to sign out from Find My Worker?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FMWError)
                ) {
                    Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = FMWNavy)
                }
            }
        )
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = FMWTextSecondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = FMWSurface,
            border = BorderStroke(1.dp, FMWBorder),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = FMWTextPrimary)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = FMWTextSecondary)
        }
        Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = null, tint = FMWTextSecondary)
    }
}
