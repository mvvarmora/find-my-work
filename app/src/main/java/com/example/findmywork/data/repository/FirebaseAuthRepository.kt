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
import com.google.firebase.firestore.FieldValue
import com.example.findmywork.data.COLLECTION_WORKERS
import com.example.findmywork.data.WorkerStatus
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
                val now = System.currentTimeMillis()
                val workerData = hashMapOf(
                    "id" to user.uid,
                    "name" to (user.displayName ?: ""),
                    "email" to (user.email ?: ""),
                    "phone" to (user.phoneNumber ?: ""),
                    "photo" to (user.photoUrl?.toString() ?: ""),
                    "age" to 0,
                    "gender" to "",
                    "categoryIds" to emptyList<String>(),
                    "experienceYears" to 0,
                    "description" to null,
                    "pricing" to null,
                    "serviceRadius" to 10.0,
                    "workingRadiusKm" to 10,
                    "isOnline" to true,
                    "ratingSum" to 0.0,
                    "ratingCount" to 0,
                    "totalJobs" to 0,
                    "completionRate" to null,
                    "totalEarnings" to null,
                    "documentsVerified" to false,
                    "bankAccount" to "",
                    "upiId" to "",
                    "city" to "",
                    "skills" to emptyList<String>(),
                    "status" to "PENDING",
                    "worksBeforeAfter" to emptyList<String>(),
                    "createdAt" to now,
                    "updatedAt" to now,
                    "active" to true,
                    "rejectionReason" to null
                )
                firestore.collection(COLLECTION_WORKERS).document(user.uid)
                    .set(workerData)
                    .await()
            }

            Result.success(isNewUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInDemo(): Result<Boolean> {
        return try {
            val authResult = auth.signInAnonymously().await()
            val user = authResult.user
            val isNew = authResult.additionalUserInfo?.isNewUser ?: false
            if (user != null) {
                val doc = firestore.collection(COLLECTION_WORKERS).document(user.uid).get().await()
                if (!doc.exists()) {
                    val now = System.currentTimeMillis()
                    val workerData = hashMapOf(
                        "id" to user.uid,
                        "name" to "Rajesh Varmora",
                        "email" to "rajesh.varmora@example.com",
                        "phone" to "+91 98765 43210",
                        "photo" to "",
                        "age" to 32,
                        "gender" to "Male",
                        "categoryIds" to listOf("electrician"),
                        "experienceYears" to 7,
                        "description" to "Master Electrician & Appliance Specialist",
                        "pricing" to 299.0,
                        "serviceRadius" to 15.0,
                        "workingRadiusKm" to 15,
                        "isOnline" to true,
                        "ratingSum" to 48.0,
                        "ratingCount" to 10,
                        "totalJobs" to 142,
                        "completionRate" to 98.4f,
                        "totalEarnings" to 184500.0,
                        "documentsVerified" to true,
                        "bankAccount" to "918273645012",
                        "upiId" to "rajesh@oksbi",
                        "city" to "Ahmedabad",
                        "skills" to listOf("Wiring", "Inverter", "MCB", "Fan Repair"),
                        "status" to "APPROVED",
                        "worksBeforeAfter" to listOf("MORNING", "EVENING"),
                        "createdAt" to now,
                        "updatedAt" to now,
                        "active" to true,
                        "rejectionReason" to null
                    )
                    firestore.collection(COLLECTION_WORKERS).document(user.uid)
                        .set(workerData)
                        .await()
                }
            }
            Result.success(false)
        } catch (e: Exception) {
            _currentUserId.value = "demo_worker_rajesh"
            Result.success(false)
        }
    }

    suspend fun logout() {
        try {
            auth.signOut()
        } catch (_: Exception) {}
        _currentUserId.value = null
    }

    companion object {
        private const val WEB_CLIENT_ID = "1039677405457-ut7nenla4eqgml27gsi7mtbc8e41qhro.apps.googleusercontent.com"
    }
}
