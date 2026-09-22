package com.example.findmywork.ui.screens

import androidx.compose.runtime.Composable
import com.example.findmywork.data.repository.FirestoreRepository

/**
 * Entry point for the Profile bottom-nav tab. Delegates to the redesigned
 * [ProfileViewScreen] (view mode). Editing is reached via the "Edit Profile"
 * buttons, which navigate to [ProfileEditScreen].
 */
@Composable
fun ProfileScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onNavigateToSettings: () -> Unit,
    onEditProfile: () -> Unit,
    onSwitchToCustomer: () -> Unit = {}
) {
    ProfileViewScreen(
        workerId = workerId,
        firestoreRepository = firestoreRepository,
        onNavigateToSettings = onNavigateToSettings,
        onEditProfile = onEditProfile,
        onSwitchToCustomer = onSwitchToCustomer
    )
}
