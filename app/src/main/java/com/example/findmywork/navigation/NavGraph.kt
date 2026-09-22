package com.example.findmywork.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import com.example.findmywork.data.repository.FirebaseAuthRepository
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.screens.ActiveJobScreen
import com.example.findmywork.ui.screens.AdminDashboardScreen
import com.example.findmywork.ui.screens.AvailableJobsScreen
import com.example.findmywork.ui.screens.CompleteProfileScreen
import com.example.findmywork.ui.screens.EarningsScreen
import com.example.findmywork.ui.screens.HomeDashboardScreen
import com.example.findmywork.ui.screens.JobDetailsScreen
import com.example.findmywork.ui.screens.JobHistoryScreen
import com.example.findmywork.ui.screens.LoginScreen
import com.example.findmywork.ui.screens.NotificationsScreen
import com.example.findmywork.ui.screens.PaymentMethodsScreen
import com.example.findmywork.ui.screens.ProfileEditScreen
import com.example.findmywork.ui.screens.ProfileScreen
import com.example.findmywork.ui.screens.SettingsScreen
import com.example.findmywork.ui.screens.SplashScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object CompleteProfile : Screen("complete_profile")
    data object HomeDashboard : Screen("home_dashboard")
    data object AvailableJobs : Screen("available_jobs")
    data object JobDetails : Screen("job_details")
    data object ActiveJob : Screen("active_job")
    data object JobHistory : Screen("job_history")
    data object Earnings : Screen("earnings")
    data object Profile : Screen("profile")
    data object ProfileEdit : Screen("profile_edit")
    data object Settings : Screen("settings")
    data object Notifications : Screen("notifications")
    data object PaymentMethods : Screen("payment_methods")
    data object AdminDashboard : Screen("admin_dashboard")
}

@Composable
fun NavGraph(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    isLoggedIn: Boolean,
    hasCompletedProfile: Boolean,
    authRepository: FirebaseAuthRepository,
    firestoreRepository: FirestoreRepository,
    workerId: String?,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onLoginSuccess: (Boolean) -> Unit = {},
    onProfileComplete: () -> Unit = {},
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentJobId by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        when (currentScreen) {
            Screen.Splash.route -> SplashScreen(
                isLoggedIn = isLoggedIn,
                hasCompletedProfile = hasCompletedProfile,
                onNavigate = onNavigate,
                onAutoGuestLogin = {
                    scope.launch {
                        authRepository.signInAsGuest()
                    }
                }
            )
            Screen.Login.route -> LoginScreen(
                authRepository = authRepository,
                onLoginSuccess = { isNewUser ->
                    onLoginSuccess(isNewUser)
                    if (isNewUser) {
                        onNavigate(Screen.CompleteProfile.route)
                    } else if (hasCompletedProfile) {
                        onNavigate(Screen.HomeDashboard.route)
                    } else {
                        onNavigate(Screen.CompleteProfile.route)
                    }
                }
            )
            Screen.CompleteProfile.route -> CompleteProfileScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onComplete = {
                    onProfileComplete()
                    onNavigate(Screen.HomeDashboard.route)
                }
            )
            Screen.HomeDashboard.route -> HomeDashboardScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onNavigateToNotifications = { onNavigate(Screen.Notifications.route) },
                onNavigateToActiveJob = { jobId ->
                    currentJobId = jobId
                    onNavigate(Screen.ActiveJob.route)
                },
                onNavigateToEarnings = { onNavigate(Screen.Earnings.route) },
                onNavigateToProfile = { onNavigate(Screen.Profile.route) },
                onNavigateToAvailableJobs = { onNavigate(Screen.AvailableJobs.route) },
                onNavigateToJobDetails = { jobId ->
                    currentJobId = jobId
                    onNavigate(Screen.JobDetails.route)
                }
            )
            Screen.AvailableJobs.route -> AvailableJobsScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onNavigateToJobDetails = { jobId ->
                    currentJobId = jobId
                    onNavigate(Screen.JobDetails.route)
                },
                onNavigateToActiveJob = { jobId ->
                    currentJobId = jobId
                    onNavigate(Screen.ActiveJob.route)
                }
            )
            Screen.JobDetails.route -> JobDetailsScreen(
                workerId = workerId,
                jobId = currentJobId,
                firestoreRepository = firestoreRepository,
                onNavigateToActiveJob = { onNavigate(Screen.ActiveJob.route) },
                onBack = { onNavigate(Screen.AvailableJobs.route) }
            )
            Screen.ActiveJob.route -> ActiveJobScreen(
                workerId = workerId,
                jobId = currentJobId,
                firestoreRepository = firestoreRepository,
                onNavigateToHome = { onNavigate(Screen.HomeDashboard.route) }
            )
            Screen.JobHistory.route -> JobHistoryScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onSelectJob = { jobId ->
                    currentJobId = jobId
                    onNavigate(Screen.JobDetails.route)
                }
            )
            Screen.Earnings.route -> EarningsScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onBack = { onNavigate(Screen.HomeDashboard.route) }
            )
            Screen.Profile.route -> ProfileScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onNavigateToSettings = { onNavigate(Screen.Settings.route) },
                onEditProfile = { onNavigate(Screen.ProfileEdit.route) },
                onNavigateToEarnings = { onNavigate(Screen.Earnings.route) },
                onNavigateToPayments = { onNavigate(Screen.PaymentMethods.route) },
                onNavigateToNotifications = { onNavigate(Screen.Notifications.route) },
                onNavigateToAdmin = { onNavigate(Screen.AdminDashboard.route) },
                onSignOut = onSignOut
            )
            Screen.ProfileEdit.route -> ProfileEditScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onBack = { onNavigate(Screen.Profile.route) }
            )
            Screen.Settings.route -> SettingsScreen(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onSignOut = onSignOut,
                onNavigateToEditProfile = { onNavigate(Screen.ProfileEdit.route) },
                onNavigateToPaymentMethods = { onNavigate(Screen.PaymentMethods.route) },
                onNavigateToNotifications = { onNavigate(Screen.Notifications.route) },
                onBack = { onNavigate(Screen.Profile.route) }
            )
            Screen.Notifications.route -> NotificationsScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onBack = { onNavigate(Screen.HomeDashboard.route) }
            )
            Screen.PaymentMethods.route -> PaymentMethodsScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onBack = { onNavigate(Screen.Settings.route) }
            )
            Screen.AdminDashboard.route -> AdminDashboardScreen(
                onBack = { onNavigate(Screen.Profile.route) }
            )
        }
    }
}
