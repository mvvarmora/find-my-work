package com.example.findmywork.ui.components.marketplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.model.Worker
import com.example.findmywork.ui.theme.*

// ─── 1. Top Bar ─────────────────────────────────────────────────────────────

@Composable
fun MarketplaceTopBar(
    customerName: String,
    currentCity: String = "Rajkot",
    walletBalance: Double = 3564.0,
    hasUnreadNotifications: Boolean = true,
    isDarkTheme: Boolean = false,
    onToggleTheme: (() -> Unit)? = null,
    onCityClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Greeting & Location
        Column {
            Text(
                text = "Hello, $customerName",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onCityClick)
                    .padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Location",
                    tint = FMWOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = currentCity,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FMWTextSecondary
                )
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = FMWTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Right: Wallet pill & Notification Bell & Theme Quick Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Theme Toggle Button
            if (onToggleTheme != null) {
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(FMWSurface)
                        .border(1.dp, FMWBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                        contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = if (isDarkTheme) FMWWarning else FMWTextPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // Wallet Pill
            Surface(
                onClick = onWalletClick,
                shape = RoundedCornerShape(20.dp),
                color = FMWSoftBlue,
                border = BorderStroke(1.dp, FMWBlue.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        tint = FMWBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = formatInr(walletBalance),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavy
                    )
                }
            }

            // Notification Bell with unread dot
            Box {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(FMWSurface)
                        .border(1.dp, FMWBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsNone,
                        contentDescription = "Notifications",
                        tint = FMWTextPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }
                if (hasUnreadNotifications) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-3).dp, y = 3.dp)
                            .clip(CircleShape)
                            .background(FMWOrange)
                            .border(1.5.dp, FMWSurface, CircleShape)
                    )
                }
            }
        }
    }
}

// ─── 2. Search Bar ──────────────────────────────────────────────────────────

@Composable
fun MarketplaceSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Find a contractor or service...",
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        color = FMWSurface,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, FMWBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = FMWTextSecondary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FMWMutedText
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = FMWNavy
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = FMWTextPrimary),
                modifier = Modifier.weight(1f)
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear",
                        tint = FMWMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FMWSoftBlue)
                    .clickable(onClick = onFilterClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "Filter",
                    tint = FMWBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ─── 3. Community Trust Banner ──────────────────────────────────────────────

@Composable
fun CommunityTrustBanner(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = FMWOrange,
        shadowElevation = 3.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.VerifiedUser,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Verified by Community",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Every review comes from homeowners who hired professionals nearby.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.92f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// ─── 4. Category Chip Row ───────────────────────────────────────────────────

@Composable
fun CategoryChipRow(
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            val isSelected = cat.equals(selectedCategory, ignoreCase = true)
            val isDark = LocalDarkTheme.current
            Surface(
                onClick = { onSelectCategory(cat) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) (if (isDark) FMWBlue else FMWNavy) else FMWSurface,
                border = BorderStroke(1.dp, if (isSelected) (if (isDark) FMWBlue else FMWNavy) else FMWBorder),
                shadowElevation = if (isSelected) 2.dp else 0.dp
            ) {
                Text(
                    text = cat,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else FMWTextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }
        }
    }
}

// ─── 5. Category Tile (Grid Item) ───────────────────────────────────────────

@Composable
fun CategoryTile(
    title: String,
    icon: ImageVector,
    colorPair: CategoryColor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = FMWSurface,
        border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.7f)),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (LocalDarkTheme.current) colorPair.icon.copy(alpha = 0.18f) else colorPair.bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = colorPair.icon,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = FMWTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── 6. Section Header ──────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = "See All",
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWBlue
                )
            }
        }
    }
}

// ─── 7. Worker Card (Featured / Top Rated) ──────────────────────────────────

@Composable
fun WorkerCard(
    worker: Worker,
    professionTitle: String = "Service Specialist",
    distanceText: String = "2.3 km away",
    priceText: String = "₹500 / service",
    onCardClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rating = if (worker.ratingCount > 0) {
        "%.1f".format(worker.ratingSum / worker.ratingCount)
    } else "4.8"
    val reviewCount = if (worker.ratingCount > 0) worker.ratingCount else 120

    Surface(
        onClick = onCardClick,
        shape = RoundedCornerShape(20.dp),
        color = FMWSurface,
        border = BorderStroke(1.dp, FMWBorder),
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with Verified badge
                WorkerAvatar(
                    name = worker.name,
                    size = 58.dp,
                    isVerified = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Info Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = worker.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        VerificationBadge()
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = professionTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Rating & Distance Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RatingBadge(rating = rating, reviewCount = reviewCount)

                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = FMWMutedText
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.NearMe,
                                contentDescription = null,
                                tint = FMWTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = distanceText,
                                style = MaterialTheme.typography.labelSmall,
                                color = FMWTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = FMWBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row: Availability + Price + CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(FMWSuccess)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Available today",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = FMWSuccess
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = priceText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavy
                    )
                }

                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FMWOrange,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Book Now",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── 8. Nearby Worker Card (Horizontal) ─────────────────────────────────────

@Composable
fun NearbyWorkerCard(
    worker: Worker,
    professionTitle: String = "Craftsman",
    distanceText: String = "1.5 km",
    ratingText: String = "4.8 (90)",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = FMWSurface,
        border = BorderStroke(1.dp, FMWBorder),
        shadowElevation = 1.dp,
        modifier = modifier.width(160.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WorkerAvatar(
                name = worker.name,
                size = 54.dp,
                isVerified = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = worker.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = professionTitle,
                style = MaterialTheme.typography.bodySmall,
                color = FMWTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = FMWStarYellow,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = ratingText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = FMWTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = distanceText,
                style = MaterialTheme.typography.labelSmall,
                color = FMWMutedText
            )
        }
    }
}

// ─── 9. Worker Avatar with Verified badge ───────────────────────────────────

@Composable
fun WorkerAvatar(
    name: String,
    size: Dp = 56.dp,
    isVerified: Boolean = false,
    modifier: Modifier = Modifier
) {
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "W" }

    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(FMWNavy, FMWBlue)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = (size.value * 0.38f).sp
                ),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (isVerified) {
            Box(
                modifier = Modifier
                    .size((size.value * 0.36f).dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Verified",
                    tint = FMWBlue,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// ─── 10. Rating & Verification Badges ────────────────────────────────────────

@Composable
fun RatingBadge(
    rating: String,
    reviewCount: Int,
    modifier: Modifier = Modifier
) {
    val isDark = LocalDarkTheme.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(FMWWarningSoft)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = FMWStarYellow,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = "$rating ($reviewCount)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309)
        )
    }
}

@Composable
fun VerificationBadge(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Rounded.Verified,
        contentDescription = "Verified Professional",
        tint = FMWBlue,
        modifier = modifier.size(17.dp)
    )
}

// ─── 11. Date Selector Strip (Horizontal) ────────────────────────────────────

data class BookingDateItem(
    val dayOfWeek: String, // "MON", "TUE"
    val dayNumber: String, // "18", "19"
    val fullDateString: String // "2026-09-18"
)

@Composable
fun DateSelectorStrip(
    dates: List<BookingDateItem>,
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(dates) { item ->
            val isSelected = item.fullDateString == selectedDate
            Surface(
                onClick = { onSelectDate(item.fullDateString) },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) FMWOrange else FMWSurface,
                border = BorderStroke(1.dp, if (isSelected) FMWOrange else FMWBorder),
                shadowElevation = if (isSelected) 3.dp else 0.dp,
                modifier = Modifier.width(62.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.dayOfWeek,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else FMWMutedText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.dayNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else FMWTextPrimary
                    )
                }
            }
        }
    }
}

// ─── 12. Time Slots Grid / Chips ────────────────────────────────────────────

@Composable
fun TimeSlotGrid(
    timeSlots: List<String>,
    selectedSlot: String,
    onSelectSlot: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        timeSlots.chunked(2).forEach { rowSlots ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowSlots.forEach { slot ->
                    val isSelected = slot == selectedSlot
                    Surface(
                        onClick = { onSelectSlot(slot) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) FMWOrange else FMWSurface,
                        border = BorderStroke(1.dp, if (isSelected) FMWOrange else FMWBorder),
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = slot,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else FMWTextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp)
                        )
                    }
                }
                if (rowSlots.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ─── 13. Booking Status Chip ────────────────────────────────────────────────

@Composable
fun BookingStatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val isDark = LocalDarkTheme.current
    val (bg, text, icon) = when (status.uppercase()) {
        "PENDING" -> Triple(FMWWarningSoft, if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309), Icons.Rounded.Schedule)
        "ACCEPTED", "CONFIRMED" -> Triple(FMWSoftBlue, if (isDark) Color(0xFF60A5FA) else FMWBlue, Icons.Rounded.CheckCircleOutline)
        "ON_THE_WAY" -> Triple(if (isDark) Color(0xFF1E1B4B) else Color(0xFFEEF2FF), if (isDark) Color(0xFFA5B4FC) else Color(0xFF4F46E5), Icons.Rounded.DirectionsCar)
        "ARRIVED", "STARTED", "WORKING" -> Triple(if (isDark) Color(0xFF2E1065) else Color(0xFFFAF5FF), if (isDark) Color(0xFFC084FC) else Color(0xFF7C3AED), Icons.Rounded.Construction)
        "COMPLETED", "RATED" -> Triple(FMWSuccessSoft, if (isDark) Color(0xFF4ADE80) else FMWSuccess, Icons.Rounded.Verified)
        "CANCELLED" -> Triple(FMWDangerSoft, if (isDark) Color(0xFFF87171) else FMWError, Icons.Rounded.Cancel)
        else -> Triple(FMWSoftBlue, if (isDark) Color(0xFF60A5FA) else FMWNavy, Icons.Rounded.Info)
    }

    val displayStatus = when (status.uppercase()) {
        "ON_THE_WAY" -> "On The Way"
        "STARTED" -> "Working"
        else -> status.replace("_", " ").lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = text,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = displayStatus,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = text
        )
    }
}

// ─── 14. Modern Floating Bottom Navigation Bar ──────────────────────────────

data class NavTab(
    val label: String,
    val icon: ImageVector,
    val activeIcon: ImageVector,
    val route: String
)

@Composable
fun MarketplaceBottomBar(
    tabs: List<NavTab>,
    currentRoute: String,
    onTabSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        color = FMWSurface,
        border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = currentRoute == tab.route
                val animatedColor by androidx.compose.animation.animateColorAsState(
                    targetValue = if (isSelected) FMWOrange else FMWTextSecondary,
                    label = "NavColor"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelect(tab.route) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isSelected) tab.activeIcon else tab.icon,
                        contentDescription = tab.label,
                        tint = animatedColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = animatedColor
                    )
                }
            }
        }
    }
}

// ─── 15. Empty State & Loading Skeleton ─────────────────────────────────────

@Composable
fun MarketplaceEmptyState(
    title: String,
    message: String,
    icon: ImageVector = Icons.Rounded.SearchOff,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(FMWSoftBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FMWBlue,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FMWTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = FMWTextSecondary,
            textAlign = TextAlign.Center
        )

        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FMWNavy,
                    contentColor = Color.White
                )
            ) {
                Text(actionText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
