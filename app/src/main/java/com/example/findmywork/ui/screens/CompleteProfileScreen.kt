package com.example.findmywork.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import com.example.findmywork.data.model.ServiceCategory
import com.example.findmywork.data.repository.FirestoreRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun CompleteProfileScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onComplete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var pricing by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var selectedCategoryIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var categories by remember { mutableStateOf<List<ServiceCategory>>(emptyList()) }
    var categoriesLoading by remember { mutableStateOf(true) }

    // Fetch service categories from Firestore
    LaunchedEffect(Unit) {
        try {
            categories = firestoreRepository.getCategories()
        } catch (_: Exception) {
            // Silently ignore — user can retry by re-entering the screen
        } finally {
            categoriesLoading = false
        }
    }

    // Pre-populate form fields if the worker already has a partial profile in Firestore
    LaunchedEffect(workerId) {
        if (workerId != null) {
            try {
                val doc = FirebaseFirestore.getInstance()
                    .collection("workers").document(workerId)
                    .get().await()
                if (doc.exists()) {
                    name = doc.getString("name") ?: ""
                    val cats = doc.get("categoryIds") as? List<String> ?: emptyList()
                    if (cats.isNotEmpty()) {
                        selectedCategoryIds = cats.toList()
                    }
                    experience = (doc.getLong("experienceYears")?.toString()) ?: ""
                    val existingSkills = doc.get("skills") as? List<String> ?: emptyList()
                    skills = existingSkills.joinToString(", ")
                    pricing = (doc.getDouble("pricing")?.toString()) ?: ""
                    radius = (doc.getDouble("serviceRadius")?.toString()) ?: ""
                    upiId = doc.getString("upiId") ?: ""
                }
            } catch (_: Exception) {
                // Silently ignore — form stays empty and user fills it in manually
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Complete Profile",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tell us about yourself to start receiving jobs",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ── Category Selection (multi-select) ──
        if (categoriesLoading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else if (categories.isEmpty()) {
            Text(
                "No service categories available",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Text(
                "Select Your Services *",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            categories.forEach { cat ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedCategoryIds = if (cat.id in selectedCategoryIds) {
                                selectedCategoryIds - cat.id
                            } else {
                                selectedCategoryIds + cat.id
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = cat.id in selectedCategoryIds,
                        onCheckedChange = {
                            selectedCategoryIds = if (cat.id in selectedCategoryIds) {
                                selectedCategoryIds - cat.id
                            } else {
                                selectedCategoryIds + cat.id
                            }
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(cat.name, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = experience, onValueChange = { experience = it },
            label = { Text("Years of Experience") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = skills, onValueChange = { skills = it },
            label = { Text("Skills (comma-separated)") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = pricing, onValueChange = { pricing = it },
            label = { Text("Hourly Rate ($)") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = radius, onValueChange = { radius = it },
            label = { Text("Service Radius (miles)") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = upiId, onValueChange = { upiId = it },
            label = { Text("UPI ID") }, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g., worker@paytm") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (errorMsg != null) {
            Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (workerId == null) return@Button
                // Validation
                if (name.isBlank()) {
                    errorMsg = "Please enter your full name"
                    return@Button
                }
                if (selectedCategoryIds.isEmpty()) {
                    errorMsg = "Please select at least one service category"
                    return@Button
                }
                isSaving = true
                errorMsg = null
                val skillsList = skills.split(",").map { it.trim() }.filter { it.isNotBlank() }
                scope.launch {
                    try {
                        firestoreRepository.saveWorkerProfile(workerId, mapOf(
                            "id" to workerId,
                            "name" to name,
                            "categoryIds" to selectedCategoryIds,
                            "skills" to skillsList,
                            "experienceYears" to (experience.toIntOrNull() ?: 0),
                            "pricing" to (pricing.toDoubleOrNull() ?: 0.0),
                            "upiId" to upiId,
                            "serviceRadius" to (radius.toDoubleOrNull() ?: 10.0),
                            "status" to "ACTIVE",
                            "updatedAt" to System.currentTimeMillis()
                        ))
                        isSaving = false
                        onComplete()
                    } catch (e: Exception) {
                        isSaving = false
                        errorMsg = e.message ?: "Failed to save"
                    }
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.height(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Save & Continue", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
