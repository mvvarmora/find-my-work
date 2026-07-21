# System Architecture

## High-Level

```
┌─────────────────────┐     ┌─────────────────┐     ┌─────────────────────┐
│   Find My Work      │     │                 │     │   Find My Worker    │
│   (Worker App)      │◄────┤   FIREBASE ☁️   ├────►│   (Customer App)    │
│   com.example...    │     │                 │     │   com.fixzo         │
│                     │     │  • Auth         │     │                     │
│  • Online toggle    │     │  • Firestore    │     │  • Browse workers   │
│  • Accept jobs      │     │  • Storage      │     │  • Create job      │
│  • Update status    │     │  • FCM          │     │  • Track progress   │
│  • View earnings    │     │  • Security     │     │  • Rate & review    │
└─────────────────────┘     └─────────────────┘     └─────────────────────┘
```

## Firebase Project: `find-my-worker-c57e0`

## App Architecture (Android - MVVM)

```
┌─────────────────────────────────────────────┐
│  UI Layer (Compose Screens)                 │
│  • Observes StateFlow from ViewModel        │
│  • Renders Loading / Content / Error states │
├─────────────────────────────────────────────┤
│  ViewModel Layer (to be added per screen)   │
│  • Holds UiState as StateFlow               │
│  • Calls repository suspend functions       │
│  • Manages snackbar/error events            │
├─────────────────────────────────────────────┤
│  Repository Layer                           │
│  • FirestoreRepository — CRUD + listeners   │
│  • AuthRepository — FirebaseAuth            │
│  • Wraps Firebase SDK in coroutines + Flow  │
├─────────────────────────────────────────────┤
│  Firebase SDK Layer                         │
│  • FirebaseAuth                             │
│  • FirebaseFirestore                        │
│  • FirebaseStorage                          │
│  • FirebaseMessaging (FCM)                  │
└─────────────────────────────────────────────┘
```

## Navigation
- 12 screens defined via `sealed class Screen`
- Simple manual routing via `currentScreen` state
- Bottom nav: Home | Jobs | History | Profile (4 tabs)
- Navigation flow: Splash → Login → CompleteProfile → Home (main loop)

## Data Flow (Job Lifecycle)

```
CUSTOMER APP                    FIREBASE                        WORKER APP
─────────────                   ────────                        ──────────
                                1. Worker logs in, sets
                                   isOnline = true  ───────────► Online
2. Browses workers ◄─── Firestore reads `workers`
                          where isOnline == true
3. Creates job:
   { service, desc,
     address, price }
   → writes `jobs/{id}`
   with status=PENDING ───────► 4. Listener fires ────────────► New job card appears!

                                5. Worker views, accepts
                                6. Updates status=ACCEPTED ────► Customer sees
                                   Sets workerId                    "Worker accepted"

7. Customer sees                 ◄──── Firestore listener
   "On the way..."

                                8. Worker updates status:
                                   ON_THE_WAY → ARRIVED
                                   → STARTED → COMPLETED
                                   (each change is LIVE for both)

9. Customer rates ◄──── writes ── 10. Earnings entry created
   rating + review                     for worker

                                11. Worker sees earning + review
```

## Key Design Decisions
1. **Firestore as single source of truth** — No local cache conflicts
2. **Snapshot listeners for real-time** — No polling, instant updates
3. **Thin-provisioned VHDX** — E: drive is mounted from `D:\my drive\my drive.vhdx`
4. **No DI framework yet** — Manual dependency injection via constructor (can upgrade to Hilt later)
5. **No backend server** — Firebase handles all async operations
