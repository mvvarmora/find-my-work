package com.example.findmywork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
            val context = androidx.compose.ui.platform.LocalContext.current
            val prefs = remember { context.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE) }
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            var isDarkTheme by remember {
                mutableStateOf(prefs.getBoolean("is_dark_theme", systemDark))
            }
            val toggleTheme: () -> Unit = {
                val newMode = !isDarkTheme
                isDarkTheme = newMode
                prefs.edit().putBoolean("is_dark_theme", newMode).apply()
            }
            var currentScreen by remember { mutableStateOf(Screen.Splash.route) }
            var hasCompletedProfile by remember { mutableStateOf(false) }

            val currentUserId by authRepository.currentUserIdFlow.collectAsState()
            val isLoggedIn = currentUserId != null

            LaunchedEffect(currentUserId) {
                if (currentUserId != null) {
                    try {
                        val firestore = FirebaseFirestore.getInstance()
                        val snap = firestore.collection("workers")
                            .document(currentUserId!!)
                            .get()
                            .await()
                        val name = snap.getString("name") ?: ""
                        val cats = snap.get("categoryIds") as? List<String> ?: emptyList()
                        hasCompletedProfile = name.isNotBlank() && cats.isNotEmpty()
                    } catch (_: Exception) {
                        hasCompletedProfile = true
                    }
                } else {
                    hasCompletedProfile = false
                }
            }

            val customerNavRoutes = listOf(
                Screen.CustomerHome.route,
                Screen.Categories.route,
                Screen.CustomerBookings.route,
                Screen.CustomerProfile.route
            )

            val providerNavRoutes = listOf(
                Screen.HomeDashboard.route,
                Screen.AvailableJobs.route,
                Screen.JobHistory.route,
                Screen.Profile.route
            )

            val showBottomBar = currentScreen in customerNavRoutes || currentScreen in providerNavRoutes
            val isProviderMode = currentScreen in providerNavRoutes

            val scope = rememberCoroutineScope()

            FindMyWorkTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(
                                currentRoute = currentScreen,
                                isProviderMode = isProviderMode,
                                onNavigate = { route -> currentScreen = route }
                            )
                        }
                    }
                ) { innerPadding ->
                    // Intelligent back handling
                    BackHandler(
                        enabled = currentScreen != Screen.CustomerHome.route &&
                                currentScreen != Screen.HomeDashboard.route &&
                                currentScreen != Screen.Splash.route &&
                                currentScreen != Screen.Login.route
                    ) {
                        currentScreen = when (currentScreen) {
                            Screen.CategoryWorkers.route -> Screen.Categories.route
                            Screen.Categories.route -> Screen.CustomerHome.route
                            Screen.WorkerDetail.route -> Screen.CustomerHome.route
                            Screen.BookingFlow.route -> Screen.WorkerDetail.route
                            Screen.CustomerBookingDetail.route -> Screen.CustomerBookings.route
                            Screen.CustomerBookings.route -> Screen.CustomerHome.route
                            Screen.AdminDashboard.route -> Screen.CustomerProfile.route
                            Screen.HelpSupport.route -> Screen.CustomerProfile.route
                            Screen.Settings.route -> Screen.CustomerProfile.route
                            Screen.ProfileEdit.route -> Screen.CustomerProfile.route
                            Screen.PaymentMethods.route -> Screen.CustomerProfile.route
                            Screen.Notifications.route -> Screen.CustomerHome.route
                            Screen.Earnings.route -> Screen.HomeDashboard.route
                            Screen.JobDetails.route -> Screen.AvailableJobs.route
                            Screen.ActiveJob.route -> Screen.HomeDashboard.route
                            Screen.CompleteProfile.route -> Screen.CustomerHome.route
                            else -> Screen.CustomerHome.route
                        }
                    }

                    NavGraph(
                        currentScreen = currentScreen,
                        onNavigate = { route -> currentScreen = route },
                        isLoggedIn = isLoggedIn,
                        hasCompletedProfile = hasCompletedProfile,
                        authRepository = authRepository,
                        firestoreRepository = firestoreRepository,
                        workerId = currentUserId,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = toggleTheme,
                        onLoginSuccess = { isNewUser ->
                            currentScreen = if (isNewUser) Screen.CompleteProfile.route else Screen.CustomerHome.route
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
