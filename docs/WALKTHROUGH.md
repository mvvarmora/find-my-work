# Project Walkthrough — Change Log

## Session 1: Project Setup & Theme
**Goal:** Get the app compiling with proper theme support

### Changes:
- Replaced mock theme with full Light/Dark theme system
- Added Light* color palettes in `Color.kt`
- `Theme.kt`: Added `LightColorScheme`, `FindMyWorkTheme` accepts `darkTheme` param
- `MainActivity.kt`: Added `isDarkTheme` state
- `NavGraph.kt`: Wired `isDarkTheme` + `onToggleTheme` to all screens
- `SettingsScreen.kt`: Added Dark Mode toggle switch

### Decisions:
- Used Material 3 color scheme
- Theme state is in-memory only (no persistence yet)
- Light palette mirrors dark palette structure exactly

---

## Session 2: Color Bug Fixes
**Goal:** Remove all hardcoded colors, use MaterialTheme.colorScheme

### Files Modified:
- All 14 screen files
- All 4 component files

### Changes:
- Replaced `Primary`, `Secondary`, `Tertiary`, `PrimaryFixed`, `TrueBlack`, `Error`
- Updated to `MaterialTheme.colorScheme.primary`, `.secondary`, etc.
- 5 deprecated `Icons.Rounded.*` → `Icons.AutoMirrored.Rounded.*`

---

## Session 3: Bug Fixes
**Goal:** Fix navigation & logic bugs

### Changes:
- `SplashScreen.kt` line ~45: Hardcoded `"login"` → `Screen.Login.route`
- `ActiveJobScreen.kt`: Added `onNavigateToHome` callback parameter
- `ActiveJobScreen.kt`: Added "Job Completed!" success state with "Back to Home" button

---

## Session 4: Firebase Dependencies
**Goal:** Add all Firebase libraries to build system

### Files Modified:
- `gradle/libs.versions.toml` — Added firebase BOM (33.12.0), auth, firestore, storage, credentials, googleid
- `build.gradle.kts` (root) — Added `google-services` plugin
- `app/build.gradle.kts` — Added all Firebase + Credential deps
- `app/google-services.json` — Created with both apps
- `gradle.properties` — JVM 2560m, parallel=false, workers.max=2

---

## Session 5: C: Drive Cleanup
**Goal:** Free disk space for build performance

### Actions:
- Verified npm redirect to E: drive (prefix + cache)
- Found old npm-cache (4.5 GB) at `Local\npm-cache` — deleted
- Found old npm-global (1.13 GB) at `Roaming\npm` — tools reinstalled to E:, then deleted
- Found AndroidStudio2025.2.2 (591 MB) — old config, deleted
- Cleared Android Studio 2025.3.3 logs (216 MB)
- Cleared TEMP + Recycle Bin (3.2 GB)
- **Result: C: 6.58 → 12.03 GB free**

### Key Discovery:
- Old npm global had tools: firebase, expo, nest, ng, pnpm, yarn, react-native, tsc
- New npm global on E: only had opencode-ai
- Reinstalled all tools to E: `npm-global` before deleting C: copy
- `D:\my drive\my drive.vhdx` (105 GB) is a VHDX mounted as E:

---

## Session 6: System Files Cleanup
**Goal:** Clean E: drive temp files inside VHDX

### Actions:
- Analyzed `E:\MV\Documents\system files` (1.3 GB total)
- `Whesvc` (Windows Hardware Error logs): 1,048 MB — cleared
- `TEMP`: 135 MB — cleared
- Chrome scoped dirs: ~38 MB — cleared
- Other temp dirs (idea-*, pip-*, overlay-*) — cleared
- **Result: 1.2 GB freed inside VHDX**

### Discovery:
- TEMP env var redirected to `E:\MV\Documents\system files`
- C: TEMP was already 0 MB

---

## Session 7: First Successful Build
**Goal:** Get the project to compile

### Issues Fixed:
- `JAVA_HOME` pointed to non-existent `C:\Program Files\Java\jdk-17`
- Changed to `C:\Program Files\Java\jdk-25`

### Result:
- `gradlew assembleDebug` — **BUILD SUCCESSFUL in 9m 44s**
- APK: `app-debug.apk` — 24.3 MB
- 36 actionable tasks executed
- Warning: "Detected multiple Kotlin daemon sessions" (minor)

---

## Session 8: Documentation & Architecture Plan
**Goal:** Create comprehensive project documentation

### Files Created:
- `docs/PROJECT_GOALS.md` — Vision, features, success criteria
- `docs/ARCHITECTURE.md` — System architecture, Firebase schema, data flow
- `docs/PLAN.md` — Detailed implementation phases
- `docs/ACTIVITY.md` — App state management per screen
- `docs/WALKTHROUGH.md` — This file (change log)
- `docs/FIRESTORE_SCHEMA.md` — Collections, indexes, security rules

---

## Session 9: Phase 1 — Firebase Auth Integration
**Goal:** Replace mock auth with real Firebase Auth + Google Sign-In

### Files Created:
- `data/repository/FirebaseAuthRepository.kt` — Real Firebase Auth with:
  - Google Sign-In via Credential Manager API
  - Email/password login & register
  - `currentUserIdFlow: StateFlow<String?>` from `AuthStateListener`
  - Auto-creates Worker document on first login (placeholder)

### Files Modified:
- `AndroidManifest.xml` — Added `INTERNET` permission
- `ui/screens/LoginScreen.kt` — Complete rewrite with Firebase auth:
  - Google Sign-In button triggers Credential Manager flow
  - Loading state (CircularProgressIndicator on button)
  - Error state (Snackbar for failures)
  - `onLoginSuccess(Boolean)` callback (Boolean = isNewUser)
- `navigation/NavGraph.kt` — Added `authRepository: FirebaseAuthRepository` param, wired to LoginScreen
- `MainActivity.kt` — Full rewrite:
  - Observes `authRepository.currentUserIdFlow` for auth state
  - Checks Firestore for `workers/{uid}` on login to determine profile status
  - Calls `authRepository.logout()` on sign out
  - Proper lifecycle cleanup with `authRepository.cleanup()` in `onDestroy()`

### Result:
- **BUILD SUCCESSFUL in 32s** (5 tasks executed, 31 cached)
- Mock auth completely replaced with real Firebase Auth
- Google Sign-In ready (needs testing on device/emulator with Google Play)
- Auto-navigation: new user → CompleteProfile, returning → Home
