package com.example.findmywork.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWNavyDeep
import com.example.findmywork.ui.theme.FMWNavyGradientEnd
import com.example.findmywork.ui.theme.FMWNavyGradientStart
import com.example.findmywork.ui.theme.FMWPrimaryLight
import com.example.findmywork.ui.theme.FMWSuccess
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary
import com.example.findmywork.ui.theme.labelCaption
import kotlinx.coroutines.launch

private sealed interface ProfileViewState {
    data object Loading : ProfileViewState
    data object Empty : ProfileViewState
    data class Ready(val profile: WorkerProfile) : ProfileViewState
    data class Error(val message: String) : ProfileViewState
}

/**
 * View mode of the worker profile: gradient hero + section cards. Data is loaded
 * from the worker Firestore document each time the screen enters composition, so
 * edits made in [ProfileEditScreen] show immediately after navigating back.
 */
@Composable
fun ProfileViewScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onNavigateToSettings: () -> Unit,
    onEditProfile: () -> Unit
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
                profile == null -> ProfileViewState.Empty
                profile.name.isBlank() && profile.categoryIds.isEmpty() -> ProfileViewState.Empty
                else -> ProfileViewState.Ready(profile)
            }
        } catch (e: Exception) {
            uiState = ProfileViewState.Error(e.message ?: "Failed to load profile")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                Button(
                    onClick = onEditProfile,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FMWNavy,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(50.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile", fontWeight = FontWeight.SemiBold)
                }
            }
        }
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
    onCopyUpi: () -> Unit,
    onShareUpi: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ProfileHero(profile, categories, onEditProfile, onNavigateToSettings) }

        item {
            FMWSectionCard(title = "About", subtitle = "Bio", onEdit = onEditProfile) {
                Text(
                    text = profile.bio.ifBlank { "Add a short bio to help customers know you better." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = FMWTextPrimary
                )
            }
        }

        item {
            FMWSectionCard(title = "Services", onEdit = onEditProfile) {
                val serviceNames = profile.categoryIds.mapNotNull { id ->
                    categories.find { it.id == id }?.name
                }.ifEmpty { profile.categoryIds }
                ReadOnlyChips(
                    items = serviceNames,
                    emptyText = "No services selected yet"
                )
            }
        }

        item {
            FMWSectionCard(title = "My Skills", onEdit = onEditProfile) {
                ReadOnlyChips(
                    items = profile.skills,
                    emptyText = "No skills added yet",
                    amber = true
                )
            }
        }

        item {
            FMWSectionCard(title = "Working Radius", onEdit = onEditProfile) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = FMWNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Up to ${profile.workingRadiusKm} km",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = FMWTextPrimary
                    )
                }
            }
        }

        item { PricingSectionView(profile, onEditProfile) }

        item { PackagesOffersSectionView(profile, onEditProfile) }

        item { PaymentSectionView(profile, onEditProfile, onCopyUpi, onShareUpi) }
    }
}

@Composable
private fun ProfileHero(
    profile: WorkerProfile,
    categories: List<ServiceCategory>,
    onEditProfile: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(FMWNavyGradientStart, FMWNavyGradientEnd)
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
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FMWAvatarPicker(
                photoUri = null,
                name = profile.name,
                size = 110.dp,
                editable = false
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = profile.name.ifBlank { "Worker" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = listOf(
                    profile.city.ifBlank { null },
                    if (profile.experienceYears > 0) "${profile.experienceYears} yr exp" else null
                ).filterNotNull().joinToString(" · ").ifBlank { "Professional" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroBadge(text = "★ ${"%.1f".format(profile.rating)}")
                if (profile.documentsVerified) HeroBadge(text = "Verified", icon = Icons.Rounded.Verified)
                HeroBadge(text = "${profile.skills.size} skills")
            }
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = onEditProfile,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier.height(46.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun HeroBadge(text: String, icon: ImageVector? = null) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FMWAmber,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun PricingSectionView(profile: WorkerProfile, onEditProfile: () -> Unit) {
    FMWSectionCard(title = "Pricing", onEdit = onEditProfile) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PricingRowView(label = "Hourly", amount = profile.hourlyRate, unit = "/hr")
            PricingRowView(label = "Monthly retainer", amount = profile.monthlyRate, unit = "/mo")
            PricingRowView(label = "Daily", amount = profile.dailyRate, unit = "/day")
            profile.perService.forEach { item ->
                PricingRowView(
                    label = item.name.ifBlank { "Custom service" },
                    amount = item.rate,
                    unit = "/visit"
                )
            }
            if (profile.hourlyRate <= 0 && profile.monthlyRate <= 0 &&
                profile.dailyRate <= 0 && profile.perService.isEmpty()
            ) {
                Text(
                    text = "Add your rates to appear in search results.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary
                )
            }
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
            color = FMWNavy
        )
    }
}

@Composable
private fun PackagesOffersSectionView(profile: WorkerProfile, onEditProfile: () -> Unit) {
    FMWSectionCard(title = "Packages & Offers", onEdit = onEditProfile) {
        if (profile.packages.isEmpty() && profile.offers.isEmpty()) {
            Text(
                text = "No packages or offers yet. Add them to attract more customers.",
                style = MaterialTheme.typography.bodySmall,
                color = FMWTextSecondary
            )
        }
        if (profile.packages.isNotEmpty()) {
            Text(
                text = "Packages",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = FMWTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            profile.packages.forEach { pkg ->
                ServicePackageCard(pkg = pkg, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
        if (profile.offers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Offers",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = FMWTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            profile.offers.forEach { offer ->
                ServiceOfferCard(offer = offer, modifier = Modifier.padding(bottom = 8.dp))
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
    FMWSectionCard(title = "Payment", onEdit = onEditProfile) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Payment,
                contentDescription = null,
                tint = FMWNavy,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (profile.upiId.isNotBlank()) profile.upiId else "UPI not set",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = FMWTextPrimary
                )
                if (profile.upiId.isNotBlank() && profile.documentsVerified) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = FMWSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Verified",
                            style = MaterialTheme.typography.labelCaption,
                            color = FMWSuccess
                        )
                    }
                }
            }
            if (profile.upiId.isNotBlank()) {
                OutlinedButton(
                    onClick = onCopyUpi,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onShareUpi,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FMWNavy,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (profile.bankAccountNumber.isNotBlank()) "Bank ••••${profile.bankAccountNumber.takeLast(4)}"
                    else "Bank account not set",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = FMWTextPrimary
                )
                if (profile.bankHolderName.isNotBlank() || profile.bankIfsc.isNotBlank()) {
                    Text(
                        text = listOf(profile.bankHolderName, profile.bankIfsc)
                            .filter { it.isNotBlank() }
                            .joinToString(" · "),
                        style = MaterialTheme.typography.labelCaption,
                        color = FMWTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Accept after-hours jobs",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = FMWTextPrimary
                )
                Text(
                    text = if (profile.availableAfterHours) "Enabled" else "Disabled",
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary
                )
            }
            Switch(
                checked = profile.availableAfterHours,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = FMWNavy,
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                )
            )
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
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.forEach { item ->
            Surface(
                shape = RoundedCornerShape(50),
                color = if (amber) MaterialTheme.colorScheme.surfaceVariant else FMWPrimaryLight,
                border = BorderStroke(1.dp, FMWBorder)
            ) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (amber) FMWNavyDeep else FMWNavy,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
