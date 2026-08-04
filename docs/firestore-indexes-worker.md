# Firestore Indexes — Worker App (com.example.findmywork)

Required composite indexes for the Worker App Firestore queries.

## `jobs` Collection

### Available Jobs (status query)
```
Collection: jobs
Fields:
  - status ASC
  - createdAt DESC
```
**Purpose:** Queries for available/open jobs sorted by most recent first.

### Active Jobs (worker assignment)
```
Collection: jobs
Fields:
  - workerId ASC
  - status ASC
```
**Purpose:** Queries for jobs assigned to a specific worker, filtered by status (e.g., "in_progress", "completed").

## `notifications` Collection

### User Notifications
```
Collection: notifications
Fields:
  - userId ASC
  - createdAt DESC
```
**Purpose:** Queries notifications for a specific user, sorted by most recent first.

## `earnings` Collection

### Worker Earnings History
```
Collection: earnings
Fields:
  - workerId ASC
  - date DESC
```
**Purpose:** Queries earnings records for a specific worker, sorted by date descending.

---

## How to Deploy

1. Open **Firebase Console** → **Firestore Database** → **Indexes** tab.
2. Click **Add Index** and configure each composite index listed above.
3. Alternatively, use the Firebase CLI:

```bash
firebase firestore:indexes
```

This will display the current `firestore.indexes.json`. Add the indexes above to that file and deploy with:

```bash
firebase deploy --only firestore:indexes
```

## Example `firestore.indexes.json`

```json
{
  "indexes": [
    {
      "collectionGroup": "jobs",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "status", "order": "ASCENDING" },
        { "fieldPath": "createdAt", "order": "DESCENDING" }
      ]
    },
    {
      "collectionGroup": "jobs",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "workerId", "order": "ASCENDING" },
        { "fieldPath": "status", "order": "ASCENDING" }
      ]
    },
    {
      "collectionGroup": "notifications",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "userId", "order": "ASCENDING" },
        { "fieldPath": "createdAt", "order": "DESCENDING" }
      ]
    },
    {
      "collectionGroup": "earnings",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "workerId", "order": "ASCENDING" },
        { "fieldPath": "date", "order": "DESCENDING" }
      ]
    }
  ]
}
```
