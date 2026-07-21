# Changelog

## [1.0.0] - 2026-07-21

### Added
- Phase 0: Project setup — Firebase deps, theme system (light/dark), icon fixes, Gradle config
- Phase 1: Google Sign-In via Credential Manager API, auto-create worker doc on first login
- Phase 2: Firestore repository with real-time snapshot listeners for workers, jobs, earnings, notifications
- Phase 3: Complete Profile screen (save to Firestore), Profile screen (read from Firestore)
- Phase 4: Full job flow — dashboard, available jobs, job details, active job timeline, job history, earnings, notifications
- Phase 5: Online/Offline toggle with lifecycle-aware auto-offline and active job protection
- App logo (512x512 circular) on Splash and Login screens
- Light/Dark theme toggle in Settings
- `opencode.jsonc` with LSP, MCP, and project rules configuration

### Fixed
- Firestore `callbackFlow` crash: `close(error)` → `return@addSnapshotListener`
- Invalid Firestore query in `getCompletedJobsFlow`: removed incompatible `orderBy("updatedAt")` with `whereIn`
- ExampleUnitTest.kt broken syntax (all-on-one-line)

### Changed
- Auth: Removed email/password, Google Sign-In only
- Navigation: All screens use real Firestore data with `workerId` and `firestoreRepository` from NavGraph

### Technical
- Kotlin 2.2.10, Compose BOM 2026.02.01, Gradle 9.3.1, JDK 25
- Firebase BOM 33.12.0, Firestore, Auth, Storage
- Architecture: ViewModel + StateFlow + Jetpack Compose + Material 3
- Build APK: 24.3 MB (debug)
