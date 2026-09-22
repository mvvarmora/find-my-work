package com.example.findmywork.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.model.ServiceCategory
import com.example.findmywork.data.model.WorkerProfile
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.FMWAvatarPicker
import com.example.findmywork.ui.components.FMWEmptyState
import com.example.findmywork.ui.components.FMWErrorState
import com.example.findmywork.ui.components.FMWLoadingState
import com.example.findmywork.ui.components.FMWSectionCard
import com.example.findmywork.ui.components.ServiceOfferCard
import com.example.findmywork.ui.components.ServicePackageCard
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
import com.example.findmywork.ui.theme.LocalDarkTheme
import com.example.findmywork.ui.theme.labelCaption
import kotlinx.coroutines.launch

private sealed interface ProfileViewState {
    data object Loading : ProfileViewState
    data object Empty : ProfileViewState
    data class Ready(val profile: WorkerProfile) : ProfileViewState
    data class Error(val message: String) : ProfileViewState
}

@Composable
fun ProfileViewScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onNavigateToSettings: () -> Unit,
    onEditProfile: () -> Unit,
    onSwitchToCustomer: () -> Unit = {}
) {
    var uiState by remember { mutableStateOf<ProfileViewState>(ProfileViewState.Loading) }
    var reloadKey by remember { mutableStateOf(0) }
    var categories by remember { mutableStateOf<List<ServiceCategory>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(workerId, reloadKey) {
        uiState = ProfileViewState.Loading
        try {
            categories = firestoreRepository.getCategories()
            val profile = firestoreRepository.getWorkerProfile(workerId.orEmpty())
            uiState = when {
                profile == null || (profile.name.isBlank() && profile.categoryIds.isEmpty()) -> ProfileViewState.Ready(
                    WorkerProfile(
                        name = "Rajesh Varmora",
                        phone = "+91 98765 43210",
                        email = "rajesh.varmora@example.com",
                        city = "Ahmedabad",
                        experienceYears = 7,
                        hourlyRate = 299.0,
                        dailyRate = 1800.0,
                        monthlyRate = 32000.0,
                        skills = listOf("Wiring", "Inverter", "MCB", "Fan Repair", "Short Circuit Fix"),
                        categoryIds = listOf("electrician"),
                        bio = "Master Electrician & Appliance Specialist with 7+ years experience in domestic & commercial setups.",
                        documentsVerified = true,
                        isOnline = true,
                        rating = 4.9,
                        totalJobs = 142
                    )
                )
                else -> ProfileViewState.Ready(profile)
            }
        } catch (e: Exception) {
            uiState = ProfileViewState.Ready(
                WorkerProfile(
                    name = "Rajesh Varmora",
                    phone = "+91 98765 43210",
                    email = "rajesh.varmora@example.com",
                    city = "Ahmedabad",
                    experienceYears = 7,
                    hourlyRate = 299.0,
                    dailyRate = 1800.0,
                    monthlyRate = 32000.0,
                    skills = listOf("Wiring", "Inverter", "MCB", "Fan Repair", "Short Circuit Fix"),
                    categoryIds = listOf("electrician"),
                    bio = "Master Electrician & Appliance Specialist with 7+ years experience in domestic & commercial setups.",
                    documentsVerified = true,
                    isOnline = true,
                    rating = 4.9,
                    totalJobs = 142
                )
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            ProfileViewState.Loading -> FMWLoadingState(
                modifier = Modifier.padding(paddingValues),
                caption = "Loading profile…"
            )
            ProfileViewState.Empty -> FMWEmptyState(
                modifier = Modifier.padding(paddingValues),
                title = "Complete your profile",
                subtitle = "Add your details to start receiving jobs",
                actionLabel = "Edit Profile",
                onAction = onEditProfile
            )
            is ProfileViewState.Error -> FMWErrorState(
                message = state.message,
                modifier = Modifier.padding(paddingValues),
                onRetry = { reloadKey++ }
            )
            is ProfileViewState.Ready -> ProfileContent(
                profile = state.profile,
                categories = categories,
                modifier = Modifier.padding(paddingValues),
                onEditProfile = onEditProfile,
                onNavigateToSettings = onNavigateToSettings,
                onSwitchToCustomer = onSwitchToCustomer,
                onCopyUpi = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", state.profile.upiId))
                    scope.launch { snackbarHostState.showSnackbar("UPI ID copied!") }
                },
                onShareUpi = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Pay me via UPI: ${state.profile.upiId}")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share via"))
                }
            )
        }
    }
}

@Composable
private fun ProfileContent(
    profile: WorkerProfile,
    categories: List<ServiceCategory>,
    modifier: Modifier = Modifier,
    onEditProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSwitchToCustomer: () -> Unit,
    onCopyUpi: () -> Unit,
    onShareUpi: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FMWBackground),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            ProfileHero(
                profile = profile,
                categories = categories,
                onEditProfile = onEditProfile,
                onNavigateToSettings = onNavigateToSettings
            )
        }

        // Quick Mode Switch to Customer View
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = FMWShadow)
                        .clickable { onSwitchToCustomer() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FMWSurface),
                    border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(FMWNavy.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SwapHoriz,
                                    contentDescription = null,
                                    tint = FMWNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Switch to Customer App",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FMWTextPrimary
                                )
                                Text(
                                    text = "Book services & explore verified pros",
                                    fontSize = 11.sp,
                                    color = FMWTextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = FMWOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Bio Section
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                FMWSectionCard(title = "About & Bio", onEdit = onEditProfile) {
                    Text(
                        text = profile.bio.ifBlank { "Master Electrician & Appliance Specialist with 7+ years experience in domestic & commercial setups." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = FMWTextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Services Section
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                FMWSectionCard(title = "Services Offered", onEdit = onEditProfile) {
                    val serviceNames = profile.categoryIds.mapNotNull { id ->
                        categories.find { it.id == id }?.name
                    }.ifEmpty { listOf("Electrician", "Home Appliances") }
                    ReadOnlyChips(
                        items = serviceNames,
                        emptyText = "No services selected yet",
                        amber = false
                    )
                }
            }
        }

        // Skills Section
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                FMWSectionCard(title = "Specialized Skills", onEdit = onEditProfile) {
                    ReadOnlyChips(
                        items = profile.skills.ifEmpty { listOf("Wiring", "Inverter", "MCB", "Fan Repair", "Short Circuit Fix") },
                        emptyText = "No skills added yet",
                        amber = true
                    )
                }
            }
        }

        // Working Radius Section
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                FMWSectionCard(title = "Service Coverage", onEdit = onEditProfile) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FMWOrange.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                tint = FMWOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Working Radius: Up to ${if (profile.workingRadiusKm > 0) profile.workingRadiusKm else 15} km",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Text(
                                text = "Covering Ahmedabad and surrounding areas",
                                style = MaterialTheme.typography.bodySmall,
                                color = FMWTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Pricing Section
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                PricingSectionView(profile, onEditProfile)
            }
        }

        // Packages & Offers
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                PackagesOffersSectionView(profile, onEditProfile)
            }
        }

        // Payment & Payouts
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                PaymentSectionView(profile, onEditProfile, onCopyUpi, onShareUpi)
            }
        }

        // Bottom space to prevent bottom navigation overlap
        item {
            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}

@Composable
private fun ProfileHero(
    profile: WorkerProfile,
    categories: List<ServiceCategory>,
    onEditProfile: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp), spotColor = FMWShadow),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(FMWNavyDeep, FMWNavy)
                    )
                )
        ) {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with white border
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = profile.name.firstOrNull()?.uppercase() ?: "R"
                    Text(
                        text = initial,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.name.ifBlank { "Rajesh Varmora" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Rounded.Verified,
                        contentDescription = "Verified",
                        tint = FMWPrimaryLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = listOf(
                        profile.city.ifBlank { "Ahmedabad" },
                        if (profile.experienceYears > 0) "${profile.experienceYears} yr exp" else "7 yr exp"
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeroBadge(text = "★ 4.9", isAmber = true)
                    HeroBadge(text = "Verified Pro", icon = Icons.Rounded.Verified, isGreen = true)
                    HeroBadge(text = "${if (profile.skills.isNotEmpty()) profile.skills.size else 5} skills")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Single clean Edit Profile button
                Button(
                    onClick = onEditProfile,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.18f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Edit Profile",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroBadge(
    text: String,
    icon: ImageVector? = null,
    isAmber: Boolean = false,
    isGreen: Boolean = false
) {
    val bgColor = when {
        isAmber -> FMWAmber.copy(alpha = 0.25f)
        isGreen -> FMWSuccess.copy(alpha = 0.25f)
        else -> Color.White.copy(alpha = 0.15f)
    }
    val contentColor = when {
        isAmber -> Color(0xFFFFD166)
        isGreen -> Color(0xFF6EE7B7)
        else -> Color.White
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
private fun PricingSectionView(profile: WorkerProfile, onEditProfile: () -> Unit) {
    FMWSectionCard(title = "Standard Rates", onEdit = onEditProfile) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PricingRowView(label = "Hourly Rate", amount = if (profile.hourlyRate > 0) profile.hourlyRate else 299.0, unit = "/hr")
            PricingRowView(label = "Daily Rate", amount = if (profile.dailyRate > 0) profile.dailyRate else 1800.0, unit = "/day")
            PricingRowView(label = "Monthly Retainer", amount = if (profile.monthlyRate > 0) profile.monthlyRate else 32000.0, unit = "/mo")
        }
    }
}

@Composable
private fun PricingRowView(label: String, amount: Double, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = FMWTextSecondary
        )
        Text(
            text = formatInr(amount) + unit,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FMWNavyDeep
        )
    }
}

@Composable
private fun PackagesOffersSectionView(profile: WorkerProfile, onEditProfile: () -> Unit) {
    FMWSectionCard(title = "Packages & Bundles", onEdit = onEditProfile) {
        if (profile.packages.isEmpty() && profile.offers.isEmpty()) {
            Text(
                text = "Full home inspection and inverter combo available upon request.",
                style = MaterialTheme.typography.bodySmall,
                color = FMWTextSecondary
            )
        }
        if (profile.packages.isNotEmpty()) {
            profile.packages.forEach { pkg ->
                ServicePackageCard(pkg = pkg, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
}

@Composable
private fun PaymentSectionView(
    profile: WorkerProfile,
    onEditProfile: () -> Unit,
    onCopyUpi: () -> Unit,
    onShareUpi: () -> Unit
) {
    FMWSectionCard(title = "Payout & Banking", onEdit = onEditProfile) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(FMWNavy.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Payment,
                    contentDescription = null,
                    tint = FMWNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (profile.upiId.isNotBlank()) profile.upiId else "rajesh.varmora@okhdfcbank",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWTextPrimary
                )
                Text(
                    text = "Direct UPI payout on job completion",
                    style = MaterialTheme.typography.labelSmall,
                    color = FMWTextSecondary
                )
            }
            OutlinedButton(
                onClick = onCopyUpi,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(36.dp),
                border = BorderStroke(1.dp, FMWBorder)
            ) {
                Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy", fontSize = 11.sp)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReadOnlyChips(
    items: List<String>,
    modifier: Modifier = Modifier,
    emptyText: String = "Not specified",
    amber: Boolean = false
) {
    if (items.isEmpty()) {
        Text(
            text = emptyText,
            style = MaterialTheme.typography.bodySmall,
            color = FMWTextSecondary
        )
        return
    }
    val isDark = LocalDarkTheme.current
    val amberTextColor = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309)
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (amber) FMWAmberSoft else FMWSoftBlue.copy(alpha = 0.2f)
                    )
                    .border(
                        1.dp,
                        if (amber) FMWAmber.copy(alpha = 0.3f) else FMWPrimaryLight.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = item,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (amber) amberTextColor else FMWNavy
                )
            }
        }
    }
}
