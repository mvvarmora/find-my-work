package com.example.findmywork.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.findmywork.data.model.ServiceCategory
import com.example.findmywork.data.model.ServiceOffer
import com.example.findmywork.data.model.ServicePackage
import com.example.findmywork.data.model.ServicePricing
import com.example.findmywork.data.model.WorkerProfile
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.FMWAddableChipFlow
import com.example.findmywork.ui.components.FMWBackHeader
import com.example.findmywork.ui.components.FMWChipFlow
import com.example.findmywork.ui.components.FMWDropdownField
import com.example.findmywork.ui.components.FMWEditField
import com.example.findmywork.ui.components.FMWErrorState
import com.example.findmywork.ui.components.FMWLoadingState
import com.example.findmywork.ui.components.FMWSectionCard
import com.example.findmywork.ui.components.FMWAvatarPicker
import com.example.findmywork.ui.components.OfferEditor
import com.example.findmywork.ui.components.PackageEditor
import com.example.findmywork.ui.components.ServiceOfferCard
import com.example.findmywork.ui.components.ServicePackageCard
import com.example.findmywork.ui.theme.FMWDanger
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWNavyDeep
import com.example.findmywork.ui.theme.FMWTextSecondary
import com.example.findmywork.ui.theme.labelCaption
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Validation rules (kept top-level so they are unit-testable) ──

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val UPI_REGEX = Regex("^[\\w.-]+@[A-Za-z]{2,}$")

fun validateName(value: String): String? = when {
    value.isBlank() -> "Enter full name"
    value.trim().length < 3 -> "Name must be at least 3 characters"
    else -> null
}

fun validatePhone(value: String): String? {
    val trimmed = value.trim()
    return if (trimmed.length == 10 && trimmed.all { it.isDigit() }) null
    else "Enter valid 10-digit number"
}

fun validateEmail(value: String): String? =
    if (value.isBlank()) null else if (EMAIL_REGEX.matches(value)) null else "Enter valid email"

fun validateAge(value: String): String? {
    if (value.isBlank()) return null
    val age = value.toIntOrNull() ?: return "Age must be 18–80"
    return if (age in 18..80) null else "Age must be 18–80"
}

fun validateHourlyRate(value: String): String? =
    if ((value.toDoubleOrNull() ?: 0.0) > 0) null else "Enter hourly rate"

fun validateUpi(value: String): String? =
    if (value.isBlank()) null else if (UPI_REGEX.matches(value)) null else "Enter valid UPI ID"

fun validateRadius(radiusKm: Int): String? =
    if (radiusKm in 1..50) null else "Radius 1–50 km"

/** Returns the list of validation messages for a fully-built profile. */
fun validateWorkerProfile(p: WorkerProfile): List<String> = buildList {
    validateName(p.name)?.let { add(it) }
    validatePhone(p.phone)?.let { add(it) }
    validateEmail(p.email)?.let { add(it) }
    validateAge(if (p.age > 0) p.age.toString() else "")?.let { add(it) }
    validateHourlyRate(if (p.hourlyRate > 0) p.hourlyRate.toString() else "")?.let { add(it) }
    validateUpi(p.upiId)?.let { add(it) }
    validateRadius(p.workingRadiusKm)?.let { add(it) }
}

fun isWorkerProfileValid(p: WorkerProfile): Boolean = validateWorkerProfile(p).isEmpty()

// ── Form model ──

private data class ProfileEditForm(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val age: String = "",
    val gender: String = "",
    val city: String = "",
    val experienceYears: Int = 0,
    val bio: String = "",
    val photoUri: Uri? = null,
    val selectedCategoryIds: List<String> = emptyList(),
    val skills: List<String> = emptyList(),
    val workingRadiusKm: Int = 10,
    val hourlyRate: String = "",
    val monthlyRate: String = "",
    val dailyRate: String = "",
    val perService: List<ServicePricing> = emptyList(),
    val upiId: String = "",
    val bankHolderName: String = "",
    val bankAccountNumber: String = "",
    val bankIfsc: String = "",
    val availableAfterHours: Boolean = false,
    val packages: List<ServicePackage> = emptyList(),
    val offers: List<ServiceOffer> = emptyList(),
    val documentsVerified: Boolean = false
)

private fun WorkerProfile.toForm(): ProfileEditForm = ProfileEditForm(
    name = name,
    phone = phone,
    email = email,
    age = if (age > 0) age.toString() else "",
    gender = gender,
    city = city,
    experienceYears = experienceYears,
    bio = bio,
    selectedCategoryIds = categoryIds,
    skills = skills,
    workingRadiusKm = workingRadiusKm,
    hourlyRate = formatRate(hourlyRate),
    monthlyRate = formatRate(monthlyRate),
    dailyRate = formatRate(dailyRate),
    perService = perService,
    upiId = upiId,
    bankHolderName = bankHolderName,
    bankAccountNumber = bankAccountNumber,
    bankIfsc = bankIfsc,
    availableAfterHours = availableAfterHours,
    packages = packages,
    offers = offers,
    documentsVerified = documentsVerified
)

private fun ProfileEditForm.toWorkerProfile(existingPhotoUrl: String): WorkerProfile = WorkerProfile(
    name = name.trim(),
    phone = phone.trim(),
    email = email.trim(),
    age = age.toIntOrNull() ?: 0,
    gender = gender,
    city = city,
    experienceYears = experienceYears.coerceAtLeast(0),
    bio = bio.trim(),
    profilePhotoUrl = existingPhotoUrl,
    skills = skills.map { it.trim() }.filter { it.isNotBlank() }.distinct(),
    categoryIds = selectedCategoryIds,
    workingRadiusKm = workingRadiusKm.coerceIn(1, 50),
    hourlyRate = hourlyRate.toDoubleOrNull() ?: 0.0,
    monthlyRate = monthlyRate.toDoubleOrNull() ?: 0.0,
    dailyRate = dailyRate.toDoubleOrNull() ?: 0.0,
    perService = perService.map { it.copy(name = it.name.trim()) },
    upiId = upiId.trim(),
    bankHolderName = bankHolderName.trim(),
    bankAccountNumber = bankAccountNumber.trim(),
    bankIfsc = bankIfsc.trim(),
    availableAfterHours = availableAfterHours,
    packages = packages,
    offers = offers,
    documentsVerified = documentsVerified
)

private fun formatRate(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()

private sealed interface SaveState {
    data object Idle : SaveState
    data object Saving : SaveState
    data object Saved : SaveState
    data class Error(val message: String) : SaveState
}

private val GENDER_OPTIONS = listOf("Male", "Female", "Other")
private val CITY_OPTIONS = listOf(
    "Mumbai", "Delhi", "Bengaluru", "Hyderabad", "Ahmedabad", "Chennai", "Kolkata",
    "Pune", "Jaipur", "Surat", "Lucknow", "Rajkot", "Chandigarh", "Indore", "Bhopal",
    "Nagpur", "Vadodara", "Coimbatore", "Guwahati", "Kochi"
)

@Composable
fun ProfileEditScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onBack: () -> Unit
) {
    var loading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableStateOf(0) }
    var form by remember { mutableStateOf(ProfileEditForm()) }
    var categories by remember { mutableStateOf<List<ServiceCategory>>(emptyList()) }
    var existingPhotoUrl by remember { mutableStateOf("") }
    var touched by remember { mutableStateOf<Set<String>>(emptySet()) }
    var saveState by remember { mutableStateOf<SaveState>(SaveState.Idle) }
    var showPackageEditor by remember { mutableStateOf(false) }
    var editingPackageIndex by remember { mutableStateOf<Int?>(null) }
    var showOfferEditor by remember { mutableStateOf(false) }
    var bankExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(workerId, reloadKey) {
        loading = true
        loadError = null
        try {
            val profile = firestoreRepository.getWorkerProfile(workerId.orEmpty())
            if (profile != null && profile.name.isNotBlank()) {
                form = profile.toForm()
                existingPhotoUrl = profile.profilePhotoUrl
            } else {
                form = WorkerProfile(
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
                ).toForm()
            }
            categories = firestoreRepository.getCategories()
        } catch (e: Exception) {
            form = WorkerProfile(
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
            ).toForm()
            loadError = null
        } finally {
            loading = false
        }
    }

    val builtProfile = remember(form, existingPhotoUrl) { form.toWorkerProfile(existingPhotoUrl) }
    val isFormValid = isWorkerProfileValid(builtProfile)
    val saveEnabled = isFormValid && saveState == SaveState.Idle

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            SaveBar(
                saveState = saveState,
                enabled = saveEnabled,
                onCancel = onBack,
                onSave = {
                    scope.launch {
                        saveState = SaveState.Saving
                        try {
                            firestoreRepository.saveWorkerProfile(workerId.orEmpty(), builtProfile)
                            saveState = SaveState.Saved
                            snackbarHostState.showSnackbar("Profile saved ✓")
                            delay(1200)
                            onBack()
                        } catch (e: Exception) {
                            saveState = SaveState.Error(e.message ?: "Failed to save profile")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            loading -> FMWLoadingState(
                modifier = Modifier.padding(paddingValues),
                caption = "Loading profile…"
            )
            loadError != null -> FMWErrorState(
                message = loadError ?: "Failed to load profile",
                modifier = Modifier.padding(paddingValues),
                onRetry = { reloadKey++ }
            )
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
            ) {
                FMWBackHeader(
                    title = "Edit Profile",
                    onBack = onBack,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        PersonalInfoSection(
                            form = form,
                            onFormChange = { form = it },
                            touched = touched,
                            onTouch = { touched = touched + it }
                        )
                    }
                    item {
                        ServicesSkillsSection(
                            form = form,
                            onFormChange = { form = it },
                            categories = categories
                        )
                    }
                    item {
                        PricingSection(
                            form = form,
                            onFormChange = { form = it }
                        )
                    }
                    item {
                        PackagesOffersSection(
                            form = form,
                            onFormChange = { form = it },
                            showPackageEditor = showPackageEditor,
                            onShowPackageEditor = { showPackageEditor = true },
                            editingPackageIndex = editingPackageIndex,
                            onEditPackage = { index ->
                                editingPackageIndex = index
                                showPackageEditor = true
                            },
                            onPackageEditorDismiss = {
                                showPackageEditor = false
                                editingPackageIndex = null
                            },
                            showOfferEditor = showOfferEditor,
                            onShowOfferEditor = { showOfferEditor = true },
                            onOfferEditorDismiss = { showOfferEditor = false }
                        )
                    }
                    item {
                        PaymentSection(
                            form = form,
                            onFormChange = { form = it },
                            bankExpanded = bankExpanded,
                            onBankExpandedChange = { bankExpanded = it }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// ── Personal Info ──

@Composable
private fun PersonalInfoSection(
    form: ProfileEditForm,
    onFormChange: (ProfileEditForm) -> Unit,
    touched: Set<String>,
    onTouch: (String) -> Unit
) {
    FMWSectionCard(title = "Personal Info", subtitle = "How customers see you") {
        FMWAvatarPicker(
            photoUri = form.photoUri,
            name = form.name,
            editable = true,
            onPhotoPicked = { uri -> onFormChange(form.copy(photoUri = uri)) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        val nameError = if ("name" in touched) validateName(form.name) else null
        FMWEditField(
            value = form.name,
            onValueChange = { onFormChange(form.copy(name = it)) },
            label = "Full name *",
            error = nameError,
            success = nameError == null && form.name.isNotBlank(),
            onBlur = { onTouch("name") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        val phoneError = if ("phone" in touched) validatePhone(form.phone) else null
        FMWEditField(
            value = form.phone,
            onValueChange = { onFormChange(form.copy(phone = it.filter { c -> c.isDigit() }.take(10))) },
            label = "Phone *",
            keyboardType = KeyboardType.Phone,
            error = phoneError,
            success = phoneError == null && form.phone.isNotBlank(),
            onBlur = { onTouch("phone") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        val emailError = if ("email" in touched) validateEmail(form.email) else null
        FMWEditField(
            value = form.email,
            onValueChange = { onFormChange(form.copy(email = it)) },
            label = "Email",
            keyboardType = KeyboardType.Email,
            error = emailError,
            success = emailError == null && form.email.isNotBlank(),
            onBlur = { onTouch("email") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            val ageError = if ("age" in touched) validateAge(form.age) else null
            FMWEditField(
                value = form.age,
                onValueChange = { onFormChange(form.copy(age = it.filter { c -> c.isDigit() }.take(2))) },
                label = "Age",
                keyboardType = KeyboardType.Number,
                error = ageError,
                success = ageError == null && form.age.isNotBlank(),
                onBlur = { onTouch("age") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FMWDropdownField(
                value = form.gender,
                onValueChange = { onFormChange(form.copy(gender = it)) },
                label = "Gender",
                options = GENDER_OPTIONS,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            FMWDropdownField(
                value = form.city,
                onValueChange = { onFormChange(form.copy(city = it)) },
                label = "City",
                options = CITY_OPTIONS,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ExperienceStepper(
                value = form.experienceYears,
                onValueChange = { onFormChange(form.copy(experienceYears = it)) },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        FMWEditField(
            value = form.bio,
            onValueChange = { onFormChange(form.copy(bio = it.take(180))) },
            label = "Bio",
            placeholder = "Tell customers a little about your work…",
            singleLine = false,
            minLines = 3,
            maxLines = 5,
            supportingText = { Text("${form.bio.length}/180") }
        )
    }
}

@Composable
private fun ExperienceStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { onValueChange((value - 1).coerceAtLeast(0)) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Decrease experience",
                    tint = FMWNavy
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$value yr",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FMWNavy
                )
                Text(
                    text = "Experience",
                    style = MaterialTheme.typography.labelCaption,
                    color = FMWTextSecondary
                )
            }
            IconButton(
                onClick = { onValueChange((value + 1).coerceAtMost(50)) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Increase experience",
                    tint = FMWNavy
                )
            }
        }
    }
}

// ── Services & Skills ──

@Composable
private fun ServicesSkillsSection(
    form: ProfileEditForm,
    onFormChange: (ProfileEditForm) -> Unit,
    categories: List<ServiceCategory>
) {
    FMWSectionCard(title = "Services & Skills", subtitle = "Help customers find the right work for you") {
        Text(
            text = "Services *",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = FMWTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (categories.isEmpty()) {
            Text(
                text = "No service categories available",
                style = MaterialTheme.typography.bodySmall,
                color = FMWTextSecondary
            )
        } else {
            FMWChipFlow(
                items = categories.map { it.name },
                selected = form.selectedCategoryIds.mapNotNull { id ->
                    categories.find { it.id == id }?.name
                }.toSet(),
                onToggle = { name ->
                    val id = categories.find { it.name == name }?.id
                    if (id == null) return@FMWChipFlow
                    onFormChange(
                        form.copy(
                            selectedCategoryIds = if (id in form.selectedCategoryIds) {
                                form.selectedCategoryIds - id
                            } else {
                                form.selectedCategoryIds + id
                            }
                        )
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Skills",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = FMWTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        FMWAddableChipFlow(
            items = form.skills,
            onAdd = { skill ->
                onFormChange(form.copy(skills = (form.skills + skill).distinct()))
            },
            onRemove = { skill ->
                onFormChange(form.copy(skills = form.skills - skill))
            },
            addHint = "Add a skill (e.g. Plumbing)"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Working Radius",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = FMWTextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Up to ${form.workingRadiusKm} km",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FMWNavy
            )
            Text(
                text = "1–50 km",
                style = MaterialTheme.typography.labelCaption,
                color = FMWTextSecondary
            )
        }
        Slider(
            value = form.workingRadiusKm.toFloat(),
            onValueChange = { onFormChange(form.copy(workingRadiusKm = it.toInt())) },
            valueRange = 1f..50f,
            colors = SliderDefaults.colors(
                thumbColor = FMWNavy,
                activeTrackColor = FMWNavy,
                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

// ── Pricing ──

@Composable
private fun PricingSection(
    form: ProfileEditForm,
    onFormChange: (ProfileEditForm) -> Unit
) {
    FMWSectionCard(title = "Pricing", subtitle = "Your rates (₹)") {
        FMWEditField(
            value = form.hourlyRate,
            onValueChange = { onFormChange(form.copy(hourlyRate = it.filterRate())) },
            label = "Hourly rate (₹) *",
            keyboardType = KeyboardType.Decimal,
            placeholder = "e.g. 300"
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            FMWEditField(
                value = form.monthlyRate,
                onValueChange = { onFormChange(form.copy(monthlyRate = it.filterRate())) },
                label = "Monthly (₹)",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FMWEditField(
                value = form.dailyRate,
                onValueChange = { onFormChange(form.copy(dailyRate = it.filterRate())) },
                label = "Daily (₹)",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Custom per-service pricing",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = FMWTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (form.perService.isEmpty()) {
            Text(
                text = "Add custom rates for specific services.",
                style = MaterialTheme.typography.bodySmall,
                color = FMWTextSecondary
            )
        }
        form.perService.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FMWEditField(
                    value = item.name,
                    onValueChange = { name ->
                        onFormChange(form.copy(perService = form.perService.updateAt(index) { it.copy(name = name) }))
                    },
                    label = "Service",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FMWEditField(
                    value = if (item.rate > 0) formatRate(item.rate) else "",
                    onValueChange = { rate ->
                        onFormChange(form.copy(perService = form.perService.updateAt(index) { it.copy(rate = rate.filterRate().toDoubleOrNull() ?: 0.0) }))
                    },
                    label = "₹/visit",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { onFormChange(form.copy(perService = form.perService.filterIndexed { i, _ -> i != index })) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Remove ${item.name.ifBlank { "service pricing" }}",
                        tint = FMWDanger,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        OutlinedButton(
            onClick = { onFormChange(form.copy(perService = form.perService + ServicePricing())) },
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add service pricing")
        }
    }
}

// ── Packages & Offers ──

@Composable
private fun PackagesOffersSection(
    form: ProfileEditForm,
    onFormChange: (ProfileEditForm) -> Unit,
    showPackageEditor: Boolean,
    onShowPackageEditor: () -> Unit,
    editingPackageIndex: Int?,
    onEditPackage: (Int) -> Unit,
    onPackageEditorDismiss: () -> Unit,
    showOfferEditor: Boolean,
    onShowOfferEditor: () -> Unit,
    onOfferEditorDismiss: () -> Unit
) {
    FMWSectionCard(title = "Packages & Offers", subtitle = "Attract customers with deals") {
        Text(
            text = "Packages",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = FMWTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))

        if (form.packages.isNotEmpty()) {
            form.packages.forEachIndexed { index, pkg ->
                ServicePackageCard(
                    pkg = pkg,
                    onEdit = { onEditPackage(index) },
                    onDelete = { onFormChange(form.copy(packages = form.packages.filterIndexed { i, _ -> i != index })) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        if (showPackageEditor) {
            PackageEditor(
                initial = editingPackageIndex?.let { form.packages.getOrNull(it) },
                onSave = { updated ->
                    onFormChange(
                        if (editingPackageIndex != null) {
                            form.copy(packages = form.packages.updatePackageAt(editingPackageIndex!!) { updated })
                        } else {
                            form.copy(packages = form.packages + updated)
                        }
                    )
                    onPackageEditorDismiss()
                },
                onCancel = onPackageEditorDismiss,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            OutlinedButton(
                onClick = onShowPackageEditor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (form.packages.isEmpty()) "New Package" else "Add Package")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Offers",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = FMWTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))

        if (form.offers.isNotEmpty()) {
            form.offers.forEach { offer ->
                ServiceOfferCard(
                    offer = offer,
                    onDelete = { onFormChange(form.copy(offers = form.offers - offer)) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        if (showOfferEditor) {
            OfferEditor(
                onSave = { offer ->
                    onFormChange(form.copy(offers = form.offers + offer))
                    onOfferEditorDismiss()
                },
                onCancel = onOfferEditorDismiss,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            OutlinedButton(
                onClick = onShowOfferEditor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Offer")
            }
        }
    }
}

// ── Payment Info ──

@Composable
private fun PaymentSection(
    form: ProfileEditForm,
    onFormChange: (ProfileEditForm) -> Unit,
    bankExpanded: Boolean,
    onBankExpandedChange: (Boolean) -> Unit
) {
    FMWSectionCard(title = "Payment Info", subtitle = "How customers pay you") {
        val upiError = validateUpi(form.upiId)
        FMWEditField(
            value = form.upiId,
            onValueChange = { onFormChange(form.copy(upiId = it)) },
            label = "UPI ID",
            placeholder = "e.g. worker@okaxis",
            error = if (form.upiId.isNotBlank()) upiError else null,
            success = form.upiId.isNotBlank() && upiError == null
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bank Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = FMWTextSecondary
                )
                Text(
                    text = if (form.bankAccountNumber.isNotBlank()) "Account ••••${form.bankAccountNumber.takeLast(4)}"
                    else "Not added yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary
                )
            }
            Button(
                onClick = { onBankExpandedChange(!bankExpanded) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FMWNavy,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (bankExpanded) "Hide" else "Add / Edit")
            }
        }

        if (bankExpanded) {
            FMWEditField(
                value = form.bankHolderName,
                onValueChange = { onFormChange(form.copy(bankHolderName = it)) },
                label = "Account holder name"
            )
            Spacer(modifier = Modifier.height(8.dp))
            FMWEditField(
                value = form.bankAccountNumber,
                onValueChange = { onFormChange(form.copy(bankAccountNumber = it.filter { c -> c.isDigit() }.take(18))) },
                label = "Account number",
                keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(8.dp))
            FMWEditField(
                value = form.bankIfsc,
                onValueChange = { onFormChange(form.copy(bankIfsc = it.uppercase().take(11))) },
                label = "IFSC code",
                placeholder = "e.g. HDFC0001234"
            )
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
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Get notified for jobs outside standard hours",
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary
                )
            }
            Switch(
                checked = form.availableAfterHours,
                onCheckedChange = { onFormChange(form.copy(availableAfterHours = it)) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = FMWNavy,
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

// ── Sticky Save Bar ──

@Composable
private fun SaveBar(
    saveState: SaveState,
    enabled: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            if (saveState is SaveState.Error) {
                Text(
                    text = saveState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWDanger,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else if (!enabled) {
                Text(
                    text = "Complete the highlighted fields to save",
                    style = MaterialTheme.typography.labelCaption,
                    color = FMWTextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = onCancel,
                    enabled = saveState != SaveState.Saving,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onSave,
                    enabled = enabled && saveState != SaveState.Saved,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (saveState == SaveState.Saved) FMWNavyDeep else FMWNavy,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    when (saveState) {
                        SaveState.Saving -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Saving…")
                        }
                        SaveState.Saved -> Text("Saved ✓", fontWeight = FontWeight.SemiBold)
                        else -> Text("Save Changes", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Small list helpers ──

private fun List<ServicePricing>.updateAt(index: Int, transform: (ServicePricing) -> ServicePricing): List<ServicePricing> =
    mapIndexed { i, item -> if (i == index) transform(item) else item }

private fun List<ServicePackage>.updatePackageAt(index: Int, transform: (ServicePackage) -> ServicePackage): List<ServicePackage> =
    mapIndexed { i, item -> if (i == index) transform(item) else item }

private fun String.filterRate(): String = filter { c -> c.isDigit() || c == '.' }
