package com.example.findmywork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.launch
import com.example.findmywork.data.repository.FirebaseAuthRepository
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.navigation.BottomNavBar
import com.example.findmywork.navigation.NavGraph
import com.example.findmywork.navigation.Screen
import com.example.findmywork.ui.theme.FindMyWorkTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private lateinit var authRepository: FirebaseAuthRepository
    private lateinit var firestoreRepository: FirestoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authRepository = FirebaseAuthRepository()
        firestoreRepository = FirestoreRepository()

        ProcessLifecycleOwner.get().lifecycle.addObserver(OnlineStatusObserver(firestoreRepository) { authRepository.currentUserIdFlow.value })

        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }
            var currentScreen by remember { mutableStateOf(Screen.Splash.route) }
            var hasCompletedProfile by remember { mutableStateOf(false) }

            val currentUserId by authRepository.currentUserIdFlow.collectAsState()

            val isLoggedIn = currentUserId != null

            LaunchedEffect(currentUserId) {
                if (currentUserId != null) {
                    val firestore = FirebaseFirestore.getInstance()
                    val doc = firestore.collection("workers")
                        .document(currentUserId!!)
                        .get()
                    hasCompletedProfile = doc.isSuccessful && doc.result.exists()
                } else {
                    hasCompletedProfile = false
                }
            }

            val bottomNavRoutes = listOf(
                Screen.HomeDashboard.route,
                Screen.AvailableJobs.route,
                Screen.JobHistory.route,
                Screen.Profile.route
            )
            val showBottomBar = currentScreen in bottomNavRoutes

            val scope = rememberCoroutineScope()

            FindMyWorkTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(
                                currentRoute = currentScreen,
                                onNavigate = { route -> currentScreen = route }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        currentScreen = currentScreen,
                        onNavigate = { route -> currentScreen = route },
                        isLoggedIn = isLoggedIn,
                        hasCompletedProfile = hasCompletedProfile,
                        authRepository = authRepository,
                        firestoreRepository = firestoreRepository,
                        workerId = currentUserId,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme },
                        onLoginSuccess = { isNewUser ->
                            if (!isNewUser && isLoggedIn) {
                                currentScreen = Screen.HomeDashboard.route
                            }
                        },
                        onProfileComplete = { hasCompletedProfile = true },
                        onSignOut = {
                            scope.launch {
                                authRepository.logout()
                            }
                            hasCompletedProfile = false
                            currentScreen = Screen.Splash.route
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::authRepository.isInitialized) {
            authRepository.cleanup()
        }
    }
}
