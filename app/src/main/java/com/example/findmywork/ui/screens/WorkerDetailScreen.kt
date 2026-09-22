package com.example.findmywork.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.model.Worker
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.marketplace.RatingBadge
import com.example.findmywork.ui.components.marketplace.VerificationBadge
import com.example.findmywork.ui.components.marketplace.WorkerAvatar
import com.example.findmywork.ui.theme.*

@Composable
fun WorkerDetailScreen(
    workerId: String,
    firestoreRepository: FirestoreRepository,
    onNavigateBack: () -> Unit,
    onNavigateToBookingFlow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val worker by firestoreRepository.getWorkerFlow(workerId).collectAsState(initial = null)
    var selectedTab by remember { mutableIntStateOf(0) }
    var isFavorite by remember { mutableStateOf(false) }

    val tabs = listOf("About", "Working", "Portfolio", "Reviews")

    val activeWorker = worker ?: Worker(
        id = workerId,
        name = "Johan Thomas",
        phone = "+91 98251 10001",
        categoryIds = listOf("plumbing"),
        experienceYears = 8,
        description = "Specialized in professional residential and commercial plumbing for 8+ years. Exceptional craftsmanship, dependable service, and complete customer satisfaction guaranteed.",
        pricing = 500.0,
        city = "Rajkot",
        ratingSum = 588.0,
        ratingCount = 120,
        totalJobs = 250,
        completionRate = 0.98f,
        isOnline = true,
        documentsVerified = true,
        skills = listOf("Leak Repair", "Bathroom Fixtures", "Water Heater", "Pipe Replacement", "Drainage")
    )

    val ratingScore = if (activeWorker.ratingCount > 0)
        "%.1f".format(activeWorker.ratingSum / activeWorker.ratingCount)
    else "4.9"
    val reviewCount = if (activeWorker.ratingCount > 0) activeWorker.ratingCount else 120
    val formattedPrice = activeWorker.pricing?.let { "₹${it.toInt()} / service" } ?: "₹500 / service"

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp),
                color = FMWSurface,
                border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Standard Service",
                            style = MaterialTheme.typography.bodySmall,
                            color = FMWTextSecondary
                        )
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = FMWNavy
                        )
                    }

                    Button(
                        onClick = { onNavigateToBookingFlow(activeWorker.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FMWOrange,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = "Book Now",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        containerColor = FMWBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // 1. Hero Cover Image with Action Overlays
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(FMWNavy, FMWBlue, Color(0xFF1E40AF))
                            )
                        )
                ) {
                    // Decorative craftsman pattern
                    Icon(
                        imageVector = Icons.Rounded.Construction,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(160.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = 30.dp)
                    )

                    // Navigation Actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { /* Share */ },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.35f))
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = "Share",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = { isFavorite = !isFavorite },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.35f))
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) FMWOrange else Color.White
                                )
                            }
                        }
                    }
                }
            }

            // 2. Profile Avatar & Primary Info (Overlapping Cover)
            item {
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = FMWSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-24).dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WorkerAvatar(
                                name = activeWorker.name,
                                size = 68.dp,
                                isVerified = true
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = activeWorker.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = FMWTextPrimary
                                    )
                                    VerificationBadge()
                                }

                                Text(
                                    text = activeWorker.skills.joinToString(" • ").ifEmpty { "Certified Craftsman" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FMWTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.LocationOn,
                                        contentDescription = null,
                                        tint = FMWOrange,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${activeWorker.city.ifEmpty { "Rajkot" }} • 2.3 km away",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FMWTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 3. 4 Key Statistics Boxes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatBox(
                                value = "${activeWorker.totalJobs}+",
                                label = "Jobs Done",
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                value = "${activeWorker.experienceYears}+ Yrs",
                                label = "Experience",
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                value = "$ratingScore ★",
                                label = "Rating",
                                valueColor = Color(0xFFD97706),
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                value = "${(activeWorker.completionRate?.times(100) ?: 98.0).toInt()}%",
                                label = "On-Time",
                                valueColor = FMWSuccess,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 4. Tab Navigation
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = FMWSurfaceSubtle,
                            contentColor = FMWNavy,
                            indicator = {},
                            divider = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .padding(4.dp)
                        ) {
                            tabs.forEachIndexed { index, tabName ->
                                val isSelected = selectedTab == index
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) FMWSurface else Color.Transparent)
                                        .clickable { selectedTab = index }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tabName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) FMWNavy else FMWTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Tab Content
            when (selectedTab) {
                0 -> { // About Tab
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = activeWorker.description ?: "Certified home service professional offering top-tier installation and repair solutions with guaranteed quality workmanship.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FMWTextSecondary,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Skills & Expertise",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                activeWorker.skills.take(3).forEach { skill ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = FMWSoftBlue,
                                        border = BorderStroke(1.dp, FMWBlue.copy(alpha = 0.2f))
                                    ) {
                                        Text(
                                            text = skill,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = FMWBlue,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Trust Badges
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = FMWSuccessSoft,
                                border = BorderStroke(1.dp, FMWSuccess.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.VerifiedUser,
                                        contentDescription = null,
                                        tint = FMWSuccess,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Background Checked & Insured",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = FMWTextPrimary
                                        )
                                        Text(
                                            text = "Identity and technical certifications verified by Find My Worker.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FMWTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> { // Working Tab (Services & Working Hours)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Working Hours",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = FMWSurface,
                                border = BorderStroke(1.dp, FMWBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.AccessTime,
                                            contentDescription = null,
                                            tint = FMWNavy,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Monday – Saturday",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = FMWTextPrimary
                                        )
                                    }
                                    Text(
                                        text = "08:30 AM – 08:00 PM",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FMWNavy
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Standard Services & Rates",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            listOf(
                                "Basic Visit & Diagnosis" to "₹199",
                                "Minor Repair & Part Replacement" to "₹399",
                                "Full Service & Overhaul" to "₹699"
                            ).forEach { (srv, prc) ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = FMWSurface,
                                    border = BorderStroke(1.dp, FMWBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = srv,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = FMWTextPrimary
                                        )
                                        Text(
                                            text = prc,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = FMWOrange
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> { // Portfolio Tab (Completed Work)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Completed Work Portfolio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Real photos of recent jobs verified by homeowners",
                                style = MaterialTheme.typography.bodySmall,
                                color = FMWTextSecondary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            val portfolioItems = listOf(
                                "Bathroom Pipe Overhaul" to FMWNavy,
                                "Kitchen Sink Fixture" to FMWBlue,
                                "Main Water Line Setup" to FMWOrange,
                                "Regulator & Valve Replacement" to Color(0xFF4F46E5)
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(portfolioItems) { (title, color) ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = FMWSurface,
                                        border = BorderStroke(1.dp, FMWBorder),
                                        modifier = Modifier.width(180.dp)
                                    ) {
                                        Column {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(110.dp)
                                                    .background(color.copy(alpha = 0.85f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Construction,
                                                    contentDescription = null,
                                                    tint = Color.White.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(40.dp)
                                                )
                                            }
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = FMWTextPrimary,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> { // Reviews Tab
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Neighbor Reviews ($reviewCount)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FMWTextPrimary
                                )
                                RatingBadge(rating = ratingScore, reviewCount = reviewCount)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            val reviewsList = listOf(
                                Triple("Sneha Sharma", "5.0 ★ • 2 days ago", "Arrived within 30 minutes! Diagnosed the valve leak immediately and replaced it cleanly. Very polite and professional."),
                                Triple("Vikram Mehta", "5.0 ★ • 1 week ago", "Excellent craftsmanship. Saved us from a major ceiling leakage disaster. Highly recommended!"),
                                Triple("Amit Patel", "4.8 ★ • 2 weeks ago", "Clean work and transparent pricing. No unnecessary charges.")
                            )

                            reviewsList.forEach { (reviewer, meta, comment) ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = FMWSurface,
                                    border = BorderStroke(1.dp, FMWBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            WorkerAvatar(name = reviewer, size = 36.dp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = reviewer,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = FMWTextPrimary
                                                )
                                                Text(
                                                    text = meta,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = FMWTextSecondary
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = comment,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = FMWTextPrimary,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    value: String,
    label: String,
    valueColor: Color = FMWNavy,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = FMWSurfaceSubtle,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = FMWTextSecondary,
                maxLines = 1
            )
        }
    }
}
