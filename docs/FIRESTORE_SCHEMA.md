# Firestore Schema — Find My Work / Find My Worker

## Project: `find-my-worker-c57e0`

## Collections

---

### `workers`

**Path:** `workers/{workerId}` (workerId = Firebase Auth UID)

| Field | Type | Example | Notes |
|-------|------|---------|-------|
| name | String | "Raj Kumar" | From Google profile or manual entry |
| email | String | "raj@email.com" | From auth provider |
| phone | String | "9876543210" | Manual entry |
| photo | String | "https://storage.googleapis.com/..." | Firebase Storage URL |
| profession | String | "Plumber" | Free text |
| experience | Int | 5 | Years of experience |
| description | String | "Expert in pipe fitting..." | Bio / summary |
| pricing | Double | 500.0 | Service charge in ₹ |
| serviceRadius | Double | 15.0 | Max distance willing to travel (km) |
| skills | Array\<String\> | ["pipe repair","drain cleaning","water heater","fixture installation"] | Multiple skills |
| isOnline | Boolean | true | Live availability toggle |
| rating | Double | 4.5 | Average rating (0-5 scale) |
| totalJobs | Int | 120 | Lifetime completed jobs |
| completionRate | Double | 0.95 | Ratio of completed to accepted (0-1) |
| totalEarnings | Double | 45280.0 | Lifetime earnings in ₹ |
| documentsVerified | Boolean | false | Admin-verified documents |
| fcmToken | String | "device_token_here" | Firebase Cloud Messaging token |
| createdAt | Timestamp | July 20, 2026 | Account creation date |
| updatedAt | Timestamp | July 20, 2026 | Last profile update |

**Indexes:**
- `isOnline` (simple)
- `profession` (simple)
- Composite: `profession` ↑, `isOnline` ↑, `rating` ↓

**Sample Read Queries:**
```kotlin
// Customer app: find plumbers who are online
db.collection("workers")
    .whereEqualTo("profession", "Plumber")
    .whereEqualTo("isOnline", true)
    .orderBy("rating", Query.Direction.DESCENDING)

// Worker app: get own profile
db.collection("workers").document(auth.currentUser!!.uid)
    .addSnapshotListener { snapshot, _ -> ... }
```

---

### `jobs`

**Path:** `jobs/{jobId}` (auto-generated Firestore ID)

| Field | Type | Example | Notes |
|-------|------|---------|-------|
| customerId | String | "uid_customer_abc" | Firebase Auth UID of customer |
| customerName | String | "Priya Singh" | Display name |
| customerPhone | String | "9988776655" | For worker to call/text |
| workerId | String | null | Filled when worker accepts |
| service | String | "Plumbing" | Service category |
| description | String | "Kitchen sink leaking badly" | Problem details |
| address | String | "123, MG Road, Bangalore" | Full address |
| latitude | Double | 12.9716 | For geo-distance calculations |
| longitude | Double | 77.5946 | For geo-distance calculations |
| price | Double | 500.0 | Agreed service cost |
| distance | Double | 2.3 | Distance from worker (km) |
| status | String | "PENDING" | See status flow below |
| createdAt | Timestamp | July 20, 2026 | Server timestamp |
| acceptedAt | Timestamp | null | When worker accepts |
| completedAt | Timestamp | null | When work finishes |
| updatedAt | Timestamp | July 20, 2026 | On every status change |
| rating | Double | 0 | 0-5, filled by customer after completion |
| review | String | "" | Text review from customer |

**Status Flow:**
```
PENDING ──► ACCEPTED ──► ON_THE_WAY ──► ARRIVED ──► STARTED ──► COMPLETED ──► RATED
                ↑             ↑              ↑            ↑             ↑            ↑
           (worker      (worker        (worker      (worker       (worker      (customer
            accepts)     starts         arrives)     starts        marks         rates)
                         driving)                   work)         complete)
```

**Cancellation:** PENDING → CANCELLED (by customer or worker before acceptance)

**Indexes:**
- `status` (simple)
- Composite: `workerId` ↑, `status` ↑ — "Worker's active/completed jobs"
- Composite: `customerId` ↑, `status` ↑ — "Customer's job history"
- Composite: `status` ↑, `createdAt` ↓ — "Latest pending jobs"
- Composite: `workerId` ↑, `createdAt` ↓ — "Worker's job timeline"

**Sample Read Queries:**
```kotlin
// Worker: get available jobs
db.collection("jobs")
    .whereEqualTo("status", "PENDING")
    .orderBy("createdAt", Query.Direction.DESCENDING)

// Worker: get active jobs
db.collection("jobs")
    .whereEqualTo("workerId", currentUserId)
    .whereIn("status", listOf("ACCEPTED", "ON_THE_WAY", "ARRIVED", "STARTED"))

// Worker: get job history
db.collection("jobs")
    .whereEqualTo("workerId", currentUserId)
    .whereIn("status", listOf("COMPLETED", "RATED"))
    .orderBy("updatedAt", Query.Direction.DESCENDING)

// Customer: get my active jobs
db.collection("jobs")
    .whereEqualTo("customerId", currentUserId)
    .whereNotIn("status", listOf("COMPLETED", "RATED", "CANCELLED"))
```

---

### `earnings`

**Path:** `earnings/{earningId}` (auto-generated)

| Field | Type | Example |
|-------|------|---------|
| workerId | String | "uid_worker_xyz" |
| jobId | String | "job_abc_123" |
| amount | Double | 500.0 |
| customerName | String | "Priya Singh" |
| date | Timestamp | July 20, 2026 |

**Indexes:**
- Composite: `workerId` ↑, `date` ↓ — "Worker's earnings by date"
- Composite: `workerId` ↑, `date` ↓ `amount` — "Worker's earnings summary"

---

### `notifications`

**Path:** `notifications/{notificationId}` (auto-generated)

| Field | Type | Example |
|-------|------|---------|
| userId | String | "uid_worker_xyz" |
| title | String | "New Job Request" |
| message | String | "Priya Singh needs a plumber" |
| type | String | "new_job" |
| data | Map | { "jobId": "job_abc_123" } |
| read | Boolean | false |
| createdAt | Timestamp | July 20, 2026 |

**Notification Types:**
| Type | Trigger | For |
|------|---------|-----|
| `new_job` | Customer creates PENDING job | Worker |
| `job_accepted` | Worker accepts job | Customer |
| `on_the_way` | Worker updates status | Customer |
| `arrived` | Worker arrives | Customer |
| `started` | Worker starts | Customer |
| `completed` | Worker completes | Customer |
| `payment` | Job completed + rated | Worker |
| `rating` | Customer rates | Worker |

**Indexes:**
- Composite: `userId` ↑, `createdAt` ↓ — "User's recent notifications"
- Composite: `userId` ↑, `read` ↑ — "Unread notifications"

---

## Composite Indexes Summary

| Collection | Fields | Purpose |
|-----------|--------|---------|
| workers | profession ↑, isOnline ↑, rating ↓ | Browse available workers |
| jobs | workerId ↑, status ↑ | Worker's jobs by status |
| jobs | customerId ↑, status ↑ | Customer's jobs |
| jobs | status ↑, createdAt ↓ | Latest pending jobs |
| jobs | workerId ↑, createdAt ↓ | Worker timeline |
| earnings | workerId ↑, date ↓ | Earnings history |
| notifications | userId ↑, createdAt ↓ | Notification feed |
| notifications | userId ↑, read ↑ | Unread badge count |

---

## Firestore Security Rules

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Workers: anyone can read, only self can write
    match /workers/{workerId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null
        && request.auth.uid == workerId;
      allow update: if request.auth != null
        && request.auth.uid == workerId;
      allow delete: if false;
    }

    // Jobs: authenticated read, 
    // customer creates, both parties update
    match /jobs/{jobId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth != null
        && (resource.data.customerId == request.auth.uid
        || resource.data.workerId == request.auth.uid
        || resource.data.workerId == null);
      allow delete: if false;
    }

    // Earnings: worker can read their own
    match /earnings/{earningId} {
      allow read: if request.auth != null
        && resource.data.workerId == request.auth.uid;
      allow write: if request.auth != null;
    }

    // Notifications: recipient only
    match /notifications/{notificationId} {
      allow read: if request.auth != null
        && resource.data.userId == request.auth.uid;
      allow write: if request.auth != null;
    }
  }
}
```

---

## FCM Topics (Optional)
- Per-worker topic: `/topics/worker_{workerId}` (personalized push)
- All workers topic: `/topics/all_workers` (broadcast, e.g. new feature announcements)
