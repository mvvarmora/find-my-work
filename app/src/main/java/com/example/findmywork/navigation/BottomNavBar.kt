package com.example.findmywork.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.findmywork.ui.theme.*

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val route: String
)

val customerNavItems = listOf(
    BottomNavItem("Home", Icons.Rounded.Home, Icons.Rounded.Home, Screen.CustomerHome.route),
    BottomNavItem("Categories", Icons.Rounded.GridView, Icons.Rounded.GridView, Screen.Categories.route),
    BottomNavItem("Bookings", Icons.Rounded.CalendarMonth, Icons.Rounded.CalendarMonth, Screen.CustomerBookings.route),
    BottomNavItem("Profile", Icons.Rounded.PersonOutline, Icons.Rounded.Person, Screen.CustomerProfile.route)
)

val providerNavItems = listOf(
    BottomNavItem("Dashboard", Icons.Rounded.Dashboard, Icons.Rounded.Dashboard, Screen.HomeDashboard.route),
    BottomNavItem("Jobs", Icons.Rounded.WorkOutline, Icons.Rounded.Work, Screen.AvailableJobs.route),
    BottomNavItem("History", Icons.Rounded.History, Icons.Rounded.History, Screen.JobHistory.route),
    BottomNavItem("Profile", Icons.Rounded.PersonOutline, Icons.Rounded.Person, Screen.Profile.route)
)

// Legacy alias for compatibility
val bottomNavItems = customerNavItems

@Composable
fun BottomNavBar(
    currentRoute: String,
    isProviderMode: Boolean = false,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = if (isProviderMode) providerNavItems else customerNavItems

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = FMWSurface,
        border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route

                val iconColor by animateColorAsState(
                    targetValue = if (selected) FMWOrange else FMWTextSecondary,
                    label = "BottomNavIconColor"
                )
                val bgColor by animateColorAsState(
                    targetValue = if (selected) FMWAmberSoft else Color.Transparent,
                    label = "BottomNavBgColor"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, radius = 28.dp),
                            onClick = { onNavigate(item.route) }
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.icon,
                            contentDescription = item.label,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = iconColor
                    )
                }
            }
        }
    }
}
