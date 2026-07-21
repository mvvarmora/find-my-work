package com.example.findmywork.data.repository

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _currentUserId = MutableStateFlow<String?>(auth.currentUser?.uid)
    val currentUserIdFlow: StateFlow<String?> = _currentUserId.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUserId.value = firebaseAuth.currentUser?.uid
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    fun cleanup() {
        auth.removeAuthStateListener(authStateListener)
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    fun isLoggedIn(): Boolean = auth.currentUser != null
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun signInWithGoogle(activity: Activity): Result<Boolean> {
        return try {
            val credentialManager = CredentialManager.create(activity)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()

            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            val user = auth.currentUser

            if (isNewUser && user != null) {
                val workerData = hashMapOf(
                    "name" to (user.displayName ?: ""),
                    "email" to (user.email ?: ""),
                    "photo" to (user.photoUrl?.toString() ?: ""),
                    "phone" to (user.phoneNumber ?: ""),
                    "profession" to "",
                    "experience" to 0,
                    "description" to "",
                    "pricing" to 0.0,
                    "serviceRadius" to 10.0,
                    "isOnline" to false,
                    "rating" to 0.0,
                    "totalJobs" to 0,
                    "completionRate" to 0.0,
                    "totalEarnings" to 0.0,
                    "documentsVerified" to false,
                    "skills" to emptyList<String>(),
                    "createdAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )
                firestore.collection("workers").document(user.uid)
                    .set(workerData)
                    .await()
            }

            Result.success(isNewUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        auth.signOut()
        _currentUserId.value = null
    }

    companion object {
        private const val WEB_CLIENT_ID = "1039677405457-ut7nenla4eqgml27gsi7mtbc8e41qhro.apps.googleusercontent.com"
    }
}
