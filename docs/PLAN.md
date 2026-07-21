# Implementation Plan

## Phase 0: Preparation ✅ DONE
- [x] Firebase dependencies added (BOM 33.12.0)
- [x] google-services.json configured for both apps
- [x] Theme system (light/dark) implemented
- [x] All color bugs fixed (hardcoded → MaterialTheme.colorScheme)
- [x] All deprecated icons updated (Icons.Rounded → Icons.AutoMirrored.Rounded)
- [x] SplashScreen route string fixed ("login" → Screen.Login.route)
- [x] ActiveJobScreen completion state + "Back to Home" added
- [x] Build system configured (JDK 25, Gradle 9.3.1, JVM 2560m)
- [x] C: drive cleanup (+6.5 GB freed)
- [x] Global npm tools moved to E: drive
- [x] System files cleanup (+1.2 GB freed)
- [x] BUILD SUCCESSFUL — APK 24.3 MB

## Phase 1: Firebase Auth Integration ✅ DONE

### 1A: FirebaseAuthRepository
- [x] Create `repository/FirebaseAuthRepository.kt`
  - FirebaseAuth instance
  - Google Sign-In via Credential Manager API
  - Email/password login & register
  - Logout
  - `currentUserIdFlow: StateFlow<String?>`
  - Auto-detect auth state changes

### 1B: Update LoginScreen
- [x] Connect Google Sign-In button to real auth
- [x] Add loading state (CircularProgressIndicator on button)
- [x] Add error handling (Snackbar for auth failures)
- [x] Navigate to CompleteProfile on first login
- [x] Navigate to Home if returning user

### 1C: Update MainActivity
- [x] Replace mock `isLoggedIn` with real auth state observation
- [x] Auto-navigate based on auth + profile completion status
- [x] Clean up auth listener in onDestroy

### 1D: Auto-Create Worker Document (NEXT)
- [ ] On first login, create `workers/{uid}` in Firestore
- [ ] Populate from Google profile (name, email, photo)
- [ ] Default values for other fields

## Phase 2: Firestore Repository (3-4 sessions)

### 2A: Rewrite FirestoreRepository
- [ ] `getWorkerFlow()` → Firestore document snapshot
- [ ] `getAvailableJobs()` → query `status == PENDING`
- [ ] `getActiveJobs()` → query by workerId + active statuses
- [ ] `getCompletedJobs()` → query by workerId + completed statuses
- [ ] `getNotifications()` → query by userId, ordered by date
- [ ] `getJobById()` → document reference
- [ ] `updateJobStatus(jobId, newStatus)` → update Firestore
- [ ] Proper error handling and null safety

### 2B: Real-time Listeners
- [ ] Use Firestore `snapshotListener()` for all Flow methods
- [ ] Handle listener cleanup
- [ ] Map DocumentSnapshot to models

## Phase 3: Profile & Storage (2 sessions)

### 3A: CompleteProfile Screen
- [ ] Save worker details to Firestore `workers/{uid}`
- [ ] Upload profile photo to Firebase Storage
- [ ] Get download URL, save to worker document

### 3B: Profile Screen
- [ ] Display real data from Firestore
- [ ] Edit functionality
- [ ] Photo from Storage URL

## Phase 4: Job Flow (3-4 sessions)

### 4A: HomeDashboard
- [ ] Real data: worker name, earnings, current job, stats

### 4B: AvailableJobs
- [ ] Real-time PENDING jobs list
- [ ] Accept button → Firestore update

### 4C: ActiveJob
- [ ] Timeline with real Firestore status updates
- [ ] Status progression buttons

### 4D: JobHistory + Earnings
- [ ] Real completed jobs list
- [ ] Earnings aggregation

## Phase 5: Online/Offline Toggle (1 session)

- [ ] Toggle button → writes `workers/{uid}/isOnline`
- [ ] Auto-offline on app background

## Phase 6: Notifications & Polish (2 sessions)

- [ ] FCM token registration
- [ ] Real notifications from Firestore
- [ ] Loading/shimmer/error/empty states for all screens

---

# Dependencies Graph
```
Phase 1 (Auth)
    ↓
Phase 2 (Firestore Repository)
    ↓
Phase 3 (Profile + Storage) ← depends on Phase 1
    ↓
Phase 4 (Job Flow) ← depends on Phase 2
    ↓
Phase 5 (Online Toggle) ← depends on Phase 3
    ↓
Phase 6 (Notifications) ← depends on Phase 2
```

# Build Verification
After each phase: `gradlew assembleDebug` must succeed with no errors.
