# App State Management

## UiState Pattern

Every screen follows this sealed class pattern:

```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

The ViewModel exposes a `StateFlow<UiState<T>>` which the Compose screen collects.

## Screen-by-Screen State

### SplashScreen
| State | When | Behavior |
|-------|------|----------|
| Loading | App start | Logo + CircularProgressIndicator |
| → | Auth check done | Logged in → Home, else → Login |

No explicit UiState — uses a simple loading flag + navigation callback.

### LoginScreen
| State | When | UI |
|-------|------|----|
| Idle | Initial | Google Sign-In button, Terms text |
| Loading | Auth in progress | Button shows CircularProgressIndicator, inputs disabled |
| Error | Auth failed | Snackbar with error message ("Sign in failed. Please try again.") |
| Success | Auth success | Navigate: first time → CompleteProfile, returning → Home |

```kotlin
data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### CompleteProfileScreen
| State | When | UI |
|-------|------|----|
| Idle | Form empty | Empty form with placeholders |
| Saving | Uploading photo + saving | Button loading, fields disabled |
| Error | Save failed | Snackbar with error |
| Success | Profile saved | Navigate to Home |

### HomeDashboard
| State | When | UI |
|-------|------|----|
| Loading | Fetching data | Shimmer/skeleton placeholders for each section |
| Success | Data loaded | Earnings card, current job card, stats row, notifications |
| Error | Network/Firestore fail | Error card with Retry button |
| Partial | Some data loaded, some failed | Show loaded sections, error on failed ones |

```kotlin
data class HomeUiState(
    val worker: UiState<Worker> = UiState.Loading,
    val currentJob: UiState<Job?> = UiState.Loading,
    val todayEarnings: UiState<Double> = UiState.Loading,
    val stats: UiState<WorkerStats> = UiState.Loading,
    val recentNotifications: UiState<List<Notification>> = UiState.Loading
)
```

### AvailableJobs
| State | When | UI |
|-------|------|----|
| Loading | Fetching jobs | Shimmer list (3-4 placeholder cards) |
| Success | Jobs found | Real-time job cards with distance, price |
| Empty | No PENDING jobs | "No available jobs right now" illustration |
| Error | Firestore query fails | Error card + "Retry" button |

### JobDetails
| State | When | UI |
|-------|------|----|
| Loading | Fetching single job | Progress indicator |
| Success | Job loaded | Full details card + Accept button |
| Already accepted | Worker already assigned | Disabled "Assigned to another worker" |
| Error | Job not found / fail | Error with "Go Back" |

### ActiveJob (Timeline)
| State | When | UI |
|-------|------|----|
| Loading | Fetching job data | Progress indicator |
| Active | Job in progress | Timeline + status-appropriate action button |
| Completed | Status == COMPLETED | Green success card + "Back to Home" |
| Error | Status update fails | Snackbar with error, retry action |

Action buttons map:
- ACCEPTED → "On The Way"
- ON_THE_WAY → "Arrived"
- ARRIVED → "Start Work"
- STARTED → "Complete Job"

### JobHistory
| State | When | UI |
|-------|------|----|
| Loading | Fetching | Shimmer list |
| Success | Has history | Scrollable list with service, customer, amount |
| Empty | No completed jobs | "No completed jobs yet" illustration |
| Error | Fetch fails | Error + Retry |

### Earnings
| State | When | UI |
|-------|------|----|
| Loading | Calculating | Shimmer for total + list |
| Success | Data ready | Total earnings header + itemized list |
| Empty | No earnings | "Start working to see your earnings" |
| Error | Firestore fail | Error + Retry |

### Profile
| State | When | UI |
|-------|------|----|
| Loading | Fetching | Shimmer placeholder |
| Success | Profile loaded | Photo, name, profession, stats, edit option |
| Saving | Updating | Button loading state |
| Error | Save/load fail | Snackbar |

### Notifications
| State | When | UI |
|-------|------|----|
| Loading | Fetching | Shimmer list |
| Success | Has notifications | List with unread indicator (bold + dot) |
| Empty | No notifications | "No notifications yet" |
| Error | Fail | Retry |

## Global States
| State | Where | UI |
|-------|-------|----|
| Offline | Any screen | Top banner: "You are offline" |
| Auth expired | Any screen | Force navigate to Login |
| Theme change | Settings | Instant switch via `isDarkTheme` state |
