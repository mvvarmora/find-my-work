package com.example.findmywork.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.findmywork.data.repository.FirebaseAuthRepository
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.screens.*

sealed class Screen(val route: String) {
    // Onboarding & Auth
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object CompleteProfile : Screen("complete_profile")

    // Customer Marketplace Screens
    data object CustomerHome : Screen("customer_home")
    data object Categories : Screen("categories")
    data object CategoryWorkers : Screen("category_workers")
    data object WorkerDetail : Screen("worker_detail")
    data object BookingFlow : Screen("booking_flow")
    data object CustomerBookings : Screen("customer_bookings")
    data object CustomerBookingDetail : Screen("customer_booking_detail")
    data object CustomerProfile : Screen("customer_profile")
    data object AdminDashboard : Screen("admin_dashboard")
    data object HelpSupport : Screen("help_support")

    // Provider / Partner Screens (Preserved)
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
    var currentJobId by remember { mutableStateOf("RXF-24671") }
    var selectedWorkerId by remember { mutableStateOf("worker_johan") }
    var selectedCategoryId by remember { mutableStateOf("plumbing") }

    Box(modifier = modifier) {
        when (currentScreen) {
            // ── Onboarding & Auth ──
            Screen.Splash.route -> SplashScreen(
                isLoggedIn = isLoggedIn,
                hasCompletedProfile = hasCompletedProfile,
                onNavigate = onNavigate
            )
            Screen.Login.route -> LoginScreen(
                authRepository = authRepository,
                onLoginSuccess = { isNewUser ->
                    onLoginSuccess(isNewUser)
                    if (isNewUser) {
                        onNavigate(Screen.CompleteProfile.route)
                    } else {
                        onNavigate(Screen.CustomerHome.route)
                    }
                }
            )
            Screen.CompleteProfile.route -> CompleteProfileScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onComplete = {
                    onProfileComplete()
                    onNavigate(Screen.CustomerHome.route)
                }
            )

            // ── Customer Marketplace Flow ──
            Screen.CustomerHome.route -> CustomerHomeScreen(
                customerId = workerId,
                customerName = "Ronen",
                firestoreRepository = firestoreRepository,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onNavigateToWorkerDetail = { id ->
                    selectedWorkerId = id
                    onNavigate(Screen.WorkerDetail.route)
                },
                onNavigateToBookingFlow = { id ->
                    selectedWorkerId = id
                    onNavigate(Screen.BookingFlow.route)
                },
                onNavigateToBookingDetail = { id ->
                    currentJobId = id
                    onNavigate(Screen.CustomerBookingDetail.route)
                },
                onNavigateToCategories = { onNavigate(Screen.Categories.route) },
                onNavigateToCategoryWorkers = { catId ->
                    selectedCategoryId = catId
                    onNavigate(Screen.CategoryWorkers.route)
                },
                onNavigateToNotifications = { onNavigate(Screen.Notifications.route) },
                onNavigateToWallet = { onNavigate(Screen.PaymentMethods.route) }
            )

            Screen.Categories.route -> CategoriesScreen(
                onNavigateBack = { onNavigate(Screen.CustomerHome.route) },
                onSelectCategory = { catId ->
                    selectedCategoryId = catId
                    onNavigate(Screen.CategoryWorkers.route)
                }
            )

            Screen.CategoryWorkers.route -> CategoryWorkersScreen(
                categoryId = selectedCategoryId,
                firestoreRepository = firestoreRepository,
                onNavigateBack = { onNavigate(Screen.Categories.route) },
                onNavigateToWorkerDetail = { id ->
                    selectedWorkerId = id
                    onNavigate(Screen.WorkerDetail.route)
                },
                onNavigateToBookingFlow = { id ->
                    selectedWorkerId = id
                    onNavigate(Screen.BookingFlow.route)
                }
            )

            Screen.WorkerDetail.route -> WorkerDetailScreen(
                workerId = selectedWorkerId,
                firestoreRepository = firestoreRepository,
                onNavigateBack = { onNavigate(Screen.CustomerHome.route) },
                onNavigateToBookingFlow = { id ->
                    selectedWorkerId = id
                    onNavigate(Screen.BookingFlow.route)
                }
            )

            Screen.BookingFlow.route -> BookingFlowScreen(
                workerId = selectedWorkerId,
                customerId = workerId ?: "cust_ronen",
                customerName = "Ronen",
                firestoreRepository = firestoreRepository,
                onNavigateBack = { onNavigate(Screen.WorkerDetail.route) },
                onBookingSuccess = { jobId ->
                    currentJobId = jobId
                    onNavigate(Screen.CustomerBookingDetail.route)
                }
            )

            Screen.CustomerBookings.route -> CustomerBookingsScreen(
                customerId = workerId ?: "cust_ronen",
                firestoreRepository = firestoreRepository,
                onNavigateToBookingDetail = { id ->
                    currentJobId = id
                    onNavigate(Screen.CustomerBookingDetail.route)
                },
                onNavigateToHome = { onNavigate(Screen.CustomerHome.route) }
            )

            Screen.CustomerBookingDetail.route -> CustomerBookingDetailScreen(
                jobId = currentJobId,
                firestoreRepository = firestoreRepository,
                onNavigateBack = { onNavigate(Screen.CustomerBookings.route) }
            )

            Screen.CustomerProfile.route -> CustomerProfileScreen(
                customerName = "Ronen Sharma",
                customerEmail = "ronen.sharma@example.com",
                walletBalance = 3564.0,
                bookingCount = 12,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onEditProfile = { onNavigate(Screen.ProfileEdit.route) },
                onNavigateToBookings = { onNavigate(Screen.CustomerBookings.route) },
                onNavigateToPayments = { onNavigate(Screen.PaymentMethods.route) },
                onSwitchToProvider = { onNavigate(Screen.HomeDashboard.route) },
                onNavigateToAdmin = { onNavigate(Screen.AdminDashboard.route) },
                onNavigateToHelpSupport = { onNavigate(Screen.HelpSupport.route) },
                onSignOut = onSignOut
            )

            Screen.AdminDashboard.route -> AdminDashboardScreen(
                onNavigateBack = { onNavigate(Screen.CustomerProfile.route) }
            )

            Screen.HelpSupport.route -> HelpSupportScreen(
                onNavigateBack = { onNavigate(Screen.CustomerProfile.route) }
            )

            // ── Provider / Partner Dashboard (Preserved) ──
            Screen.HomeDashboard.route -> HomeDashboardScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onNavigateToNotifications = { onNavigate(Screen.Notifications.route) },
                onNavigateToActiveJob = { jobId ->
                    currentJobId = jobId
                    onNavigate(Screen.ActiveJob.route)
                },
                onNavigateToEarnings = { onNavigate(Screen.Earnings.route) },
                onNavigateToJobs = { onNavigate(Screen.AvailableJobs.route) },
                onNavigateToHistory = { onNavigate(Screen.JobHistory.route) },
                onNavigateToProfile = { onNavigate(Screen.Profile.route) },
                onSwitchToCustomer = { onNavigate(Screen.CustomerHome.route) }
            )

            Screen.AvailableJobs.route -> AvailableJobsScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onNavigateToJobDetails = { jobId ->
                    currentJobId = jobId
                    onNavigate(Screen.JobDetails.route)
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
                firestoreRepository = firestoreRepository
            )

            Screen.Earnings.route -> EarningsScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onBack = { onNavigate(Screen.HomeDashboard.route) }
            )

            Screen.Profile.route -> ProfileScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onNavigateToSettings = { onNavigate(Screen.Settings.route) },
                onEditProfile = { onNavigate(Screen.ProfileEdit.route) },
                onSwitchToCustomer = { onNavigate(Screen.CustomerHome.route) }
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
                onBack = { onNavigate(Screen.CustomerHome.route) }
            )

            Screen.PaymentMethods.route -> PaymentMethodsScreen(
                workerId = workerId,
                firestoreRepository = firestoreRepository,
                onBack = { onNavigate(Screen.CustomerProfile.route) }
            )
        }
    }
}
