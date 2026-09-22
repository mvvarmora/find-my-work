package com.example.findmywork.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.model.Worker
import com.example.findmywork.ui.theme.*

/**
 * ── Top Bar with Greeting, Location, Wallet pill, and Notification Bell ──
 */
@Composable
fun MarketplaceTopBar(
    userName: String,
    city: String = "Rajkot",
    walletBalance: Double = 3564.0,
    hasUnreadNotifications: Boolean = true,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FMWSpacing.screenHorizontal, vertical = FMWSpacing.s),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Avatar + Greeting & Location
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(FMWNavyGradientStart)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(FMWSpacing.s))

            Column {
                Text(
                    text = "Hello, ${userName.ifBlank { "Neighbor" }}!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLocationClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = FMWOrangeCTA,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "$city, India",
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select location",
                        tint = FMWTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Right: Wallet Pill & Notification Bell
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Wallet Pill
            Surface(
                shape = FMWShapes.pill,
                color = FMWOrangeSoft,
                border = androidx.compose.foundation.BorderStroke(1.dp, FMWOrangeWarm.copy(alpha = 0.3f)),
                modifier = Modifier.clickable { onWalletClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        tint = FMWOrangeCTA,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatInr(walletBalance),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(FMWSpacing.xs))

            // Notification Bell
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, FMWBorder, CircleShape)
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = FMWNavyDeep,
                    modifier = Modifier.size(20.dp)
                )
                if (hasUnreadNotifications) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                            .clip(CircleShape)
                            .background(FMWDanger)
                    )
                }
            }

            Spacer(modifier = Modifier.width(FMWSpacing.xs))

            // Theme Switcher Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, FMWBorder, CircleShape)
                    .clickable { onToggleTheme() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                    contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                    tint = if (isDarkTheme) Color(0xFFF5B942) else FMWNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * ── Large Premium Search Bar with Filter Button ──
 */
@Composable
fun MarketplaceSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit = {},
    placeholder: String = "Find a contractor or service...",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = FMWElevation.low,
                shape = FMWShapes.search,
                ambientColor = FMWShadowDefault,
                spotColor = FMWShadowDefault
            ),
        shape = FMWShapes.search,
        color = FMWBgCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, FMWBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = FMWNavy,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            androidx.compose.foundation.text.BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = FMWTextPrimary,
                    fontWeight = FontWeight.Medium
                ),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = FMWMutedText
                        )
                    }
                    innerTextField()
                }
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = FMWTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(FMWPrimaryLight)
                    .clickable { onFilterClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filter",
                    tint = FMWNavy,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * ── Verified Community Trust Banner (Orange Accent) ──
 */
@Composable
fun TrustBanner(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = FMWShapes.large,
        color = FMWOrangeSoft,
        border = androidx.compose.foundation.BorderStroke(1.dp, FMWOrangeWarm.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(FMWOrangeCTA),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Verified by the Community for Authenticity",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Every review comes from homeowners who have hired the professional nearby.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Learn more",
                tint = FMWOrangeCTA,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * ── Soft-Colored Category Tile with Icon ──
 */
@Composable
fun CategoryTile(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconTint: Color,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(76.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(
                    elevation = if (isSelected) FMWElevation.low else FMWElevation.none,
                    shape = FMWShapes.large
                )
                .clip(FMWShapes.large)
                .background(if (isSelected) FMWNavy else backgroundColor)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) FMWNavy else backgroundColor.copy(alpha = 0.5f),
                    shape = FMWShapes.large
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else iconTint,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) FMWNavy else FMWTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * ── Horizontal Category Chip ──
 */
@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = FMWShapes.pill,
        color = if (isSelected) FMWNavy else FMWBgCard,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) FMWNavy else FMWBorder
        ),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else FMWTextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * ── Rating Badge Pill (★ 4.8) ──
 */
@Composable
fun RatingBadge(
    rating: Double,
    reviewCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = FMWAmber,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = FMWTextPrimary
        )
        if (reviewCount != null) {
            Text(
                text = " ($reviewCount)",
                style = MaterialTheme.typography.labelSmall,
                color = FMWTextSecondary
            )
        }
    }
}

/**
 * ── Popular Near You Worker Card (Inspired by Reference 1) ──
 */
@Composable
fun PopularWorkerCard(
    worker: Worker,
    distanceKm: Double = 1.5,
    onCardClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rating = if (worker.ratingCount > 0) worker.ratingSum / worker.ratingCount else 4.8

    Surface(
        modifier = modifier
            .width(180.dp)
            .shadow(
                elevation = FMWElevation.low,
                shape = FMWShapes.large,
                ambientColor = FMWShadowDefault,
                spotColor = FMWShadowDefault
            )
            .clip(FMWShapes.large)
            .clickable { onCardClick() },
        shape = FMWShapes.large,
        color = FMWBgCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, FMWBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Top: Avatar + Distance Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(FMWPrimaryLight)
                        .border(1.5.dp, FMWBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = worker.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifBlank { "W" },
                        color = FMWNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Surface(
                    shape = FMWShapes.pill,
                    color = FMWCategoryGreen,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(FMWSuccess)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${String.format("%.1f", distanceKm)} km",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = FMWSuccess
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Name
            Text(
                text = worker.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Profession tag
            Text(
                text = worker.skills.firstOrNull() ?: "Craftsman Expert",
                style = MaterialTheme.typography.bodySmall,
                color = FMWTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Rating
            RatingBadge(rating = rating, reviewCount = worker.ratingCount.takeIf { it > 0 } ?: 88)

            Spacer(modifier = Modifier.height(10.dp))

            // Price & Book CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatInr(worker.pricing ?: 500.0),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavy
                    )
                    Text(
                        text = "/service",
                        style = MaterialTheme.typography.labelSmall,
                        color = FMWMutedText
                    )
                }

                Button(
                    onClick = onBookClick,
                    shape = FMWShapes.button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FMWOrangeCTA,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "Book",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * ── Nearby Worker List Row Card (Full Width) ──
 */
@Composable
fun NearbyWorkerRowCard(
    worker: Worker,
    distanceKm: Double = 2.1,
    onCardClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rating = if (worker.ratingCount > 0) worker.ratingSum / worker.ratingCount else 4.8

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = FMWElevation.low,
                shape = FMWShapes.large,
                ambientColor = FMWShadowDefault,
                spotColor = FMWShadowDefault
            )
            .clip(FMWShapes.large)
            .clickable { onCardClick() },
        shape = FMWShapes.large,
        color = FMWBgCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, FMWBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FMWPrimaryLight)
                    .border(1.dp, FMWBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = worker.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifBlank { "W" },
                    color = FMWNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = worker.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (worker.documentsVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = FMWPrimaryBlue,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Text(
                    text = worker.skills.joinToString(" • ").ifBlank { "Home Services & Repair" },
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingBadge(rating = rating, reviewCount = worker.ratingCount.takeIf { it > 0 } ?: 120)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "•", color = FMWMutedText)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${String.format("%.1f", distanceKm)} km away",
                        style = MaterialTheme.typography.labelSmall,
                        color = FMWTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right: Price & CTA
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formatInr(worker.pricing ?: 500.0),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = FMWNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onBookClick,
                    shape = FMWShapes.button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FMWOrangeCTA,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
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

/**
 * ── Section Header with Title & Optional "See All" ──
 */
@Composable
fun MarketplaceSectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = "See All",
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FMWSpacing.screenHorizontal, vertical = FMWSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FMWTextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary
                )
            }
        }

        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = FMWOrangeCTA
                )
            }
        }
    }
}

/**
 * ── Shimmer Loading Skeleton ──
 */
@Composable
fun ShimmerSkeleton(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = FMWShapes.medium
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val isDark = LocalIsDarkTheme.current
    val shimmerColors = if (isDark) {
        listOf(
            Color(0xFF1E222A),
            Color(0xFF282D38),
            Color(0xFF1E222A)
        )
    } else {
        listOf(
            Color(0xFFE8EDF5),
            Color(0xFFF7F9FC),
            Color(0xFFE8EDF5)
        )
    }

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = translateAnim, y = translateAnim)
    )

    Spacer(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

/**
 * ── Clean Marketplace Empty State ──
 */
@Composable
fun MarketplaceEmptyState(
    title: String,
    description: String,
    icon: ImageVector = Icons.Outlined.SearchOff,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
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
                .size(72.dp)
                .clip(CircleShape)
                .background(FMWPrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FMWNavy,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FMWTextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = FMWTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onActionClick,
                shape = FMWShapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FMWNavy,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = actionText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
