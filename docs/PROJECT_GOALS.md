# Find My Work — Project Goals

## Vision
A two-sided marketplace connecting **local workers** (plumbers, electricians, AC repair, etc.) with **customers** who need their services — in real-time.

## Two Apps, One Firebase Project

| App | Package | Platform | Who |
|-----|---------|----------|-----|
| **Find My Work** | `com.example.findmywork` | Android | Workers (current) |
| **Find My Worker** | `com.fixzo` | Android | Customers (future) |

## Worker App Features (Find My Work)
- Google Sign-In / Email auth
- Online/Offline availability toggle
- Browse & accept PENDING jobs
- Real-time job status lifecycle: ACCEPTED → ON_THE_WAY → ARRIVED → STARTED → COMPLETED → RATED
- View earnings dashboard
- Track job history
- Receive notifications (new jobs, payments, ratings)
- Manage profile (photo, services, pricing, skills, experience)
- Light/Dark theme toggle

## Customer App Features (Find My Worker — future)
- Browse available workers (online + nearby)
- View worker details, experience, ratings, pricing
- Create service requests (jobs)
- Track job progress in real-time
- Rate & review workers after completion

## Success Criteria
- [ ] Worker can sign in with Google and create profile
- [ ] Worker toggle goes live → visible to customer app
- [ ] Customer creates job → Worker receives instantly
- [ ] Status updates sync in real-time between both apps
- [ ] Earnings track correctly
- [ ] Push notifications work for background updates
- [ ] APK builds successfully after each phase

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Backend:** Firebase (Auth, Firestore, Storage, FCM)
- **Authentication:** Firebase Auth + Google Sign-In (Credential Manager API)
- **State Management:** ViewModel + StateFlow
- **Build:** Gradle 9.3.1 + JDK 25
- **Min SDK:** 26
- **Target SDK:** 35
