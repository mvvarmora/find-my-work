package com.example.findmywork.data.repository

import com.example.findmywork.data.model.Earning
import com.example.findmywork.data.model.Job
import com.example.findmywork.data.model.JobStatus
import com.example.findmywork.data.model.Notification
import com.example.findmywork.data.model.Review
import com.example.findmywork.data.model.Worker
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private fun <T> documentFlow(
        path: String,
        map: (String, Map<String, Any>) -> T
    ): Flow<T?> = callbackFlow {
        val docRef = firestore.document(path)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) { return@addSnapshotListener }
            if (snapshot != null && snapshot.exists()) {
                snapshot.data?.let { trySend(map(snapshot.id, it)) }
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    private fun <T> collectionFlow(
        query: com.google.firebase.firestore.Query,
        map: (String, Map<String, Any>) -> T
    ): Flow<List<T>> = callbackFlow {
        val listener = query.addSnapshotListener { snapshots, error ->
            if (error != null) { return@addSnapshotListener }
            val list = snapshots?.documents?.mapNotNull { doc ->
                doc.data?.let { map(doc.id, it) }
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    // ── Mapping helpers ──

    private fun docToWorker(id: String, data: Map<String, Any>): Worker {
        @Suppress("UNCHECKED_CAST")
        return Worker(
            id = id,
            name = data["name"] as? String ?: "",
            email = data["email"] as? String ?: "",
            phone = data["phone"] as? String ?: "",
            photo = data["photo"] as? String ?: "",
            profession = data["profession"] as? String ?: "",
            experience = (data["experience"] as? Long)?.toInt() ?: 0,
            description = data["description"] as? String ?: "",
            pricing = data["pricing"] as? Double ?: 0.0,
            serviceRadius = data["serviceRadius"] as? Double ?: 10.0,
            isOnline = data["isOnline"] as? Boolean ?: false,
            rating = (data["rating"] as? Number)?.toFloat() ?: 0f,
            totalJobs = (data["totalJobs"] as? Long)?.toInt() ?: 0,
            completionRate = (data["completionRate"] as? Number)?.toFloat() ?: 0f,
            totalEarnings = data["totalEarnings"] as? Double ?: 0.0,
            documentsVerified = data["documentsVerified"] as? Boolean ?: false,
            skills = (data["skills"] as? List<String>) ?: emptyList(),
            reviews = emptyList()
        )
    }

    private fun docToJob(id: String, data: Map<String, Any>): Job {
        return Job(
            id = id,
            customerId = data["customerId"] as? String ?: "",
            customerName = data["customerName"] as? String ?: "",
            customerPhoto = data["customerPhoto"] as? String ?: "",
            workerId = data["workerId"] as? String ?: "",
            service = data["service"] as? String ?: "",
            description = data["description"] as? String ?: "",
            address = data["address"] as? String ?: "",
            price = data["price"] as? Double ?: 0.0,
            distance = data["distance"] as? Double ?: 0.0,
            status = try {
                JobStatus.valueOf(data["status"] as? String ?: "PENDING")
            } catch (_: Exception) { JobStatus.PENDING },
            createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
            updatedAt = data["updatedAt"] as? Long ?: System.currentTimeMillis(),
            rating = (data["rating"] as? Number)?.toFloat() ?: 0f,
            review = data["review"] as? String ?: ""
        )
    }

    private fun docToNotification(id: String, data: Map<String, Any>): Notification {
        return Notification(
            id = id,
            title = data["title"] as? String ?: "",
            message = data["message"] as? String ?: "",
            type = data["type"] as? String ?: "",
            read = data["read"] as? Boolean ?: false,
            createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis()
        )
    }

    private fun docToEarning(id: String, data: Map<String, Any>): Earning {
        return Earning(
            date = data["date"] as? Long ?: System.currentTimeMillis(),
            amount = data["amount"] as? Double ?: 0.0,
            jobId = data["jobId"] as? String ?: "",
            customerName = data["customerName"] as? String ?: ""
        )
    }

    // ── Worker ──

    fun getWorkerFlow(workerId: String): Flow<Worker?> =
        documentFlow("workers/$workerId", ::docToWorker)

    suspend fun saveWorkerProfile(workerId: String, data: Map<String, Any>) {
        firestore.collection("workers").document(workerId)
            .set(data, SetOptions.merge()).await()
    }

    suspend fun updateWorkerField(workerId: String, field: String, value: Any) {
        firestore.collection("workers").document(workerId)
            .update(field, value).await()
    }

    // ── Jobs ──

    fun getAvailableJobsFlow(): Flow<List<Job>> = collectionFlow(
        firestore.collection("jobs")
            .whereEqualTo("status", "PENDING")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING),
        ::docToJob
    )

    fun getActiveJobsFlow(workerId: String): Flow<List<Job>> = collectionFlow(
        firestore.collection("jobs")
            .whereEqualTo("workerId", workerId)
            .whereIn("status", listOf("ACCEPTED", "ON_THE_WAY", "ARRIVED", "STARTED")),
        ::docToJob
    )

    fun getCompletedJobsFlow(workerId: String): Flow<List<Job>> = collectionFlow(
        firestore.collection("jobs")
            .whereEqualTo("workerId", workerId)
            .whereIn("status", listOf("COMPLETED", "RATED")),
        ::docToJob
    )

    fun getJobByIdFlow(jobId: String): Flow<Job?> =
        documentFlow("jobs/$jobId", ::docToJob)

    suspend fun acceptJob(jobId: String, workerId: String) {
        firestore.collection("jobs").document(jobId).update(
            mapOf(
                "workerId" to workerId,
                "status" to "ACCEPTED",
                "acceptedAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun updateJobStatus(jobId: String, status: JobStatus) {
        val updates = mutableMapOf<String, Any>(
            "status" to status.name,
            "updatedAt" to System.currentTimeMillis()
        )
        if (status == JobStatus.COMPLETED) {
            updates["completedAt"] = System.currentTimeMillis()
        }
        firestore.collection("jobs").document(jobId).update(updates).await()

        if (status == JobStatus.COMPLETED) {
            val snap = firestore.collection("jobs").document(jobId).get().await()
            val d = snap.data ?: return
            val wid = d["workerId"] as? String ?: return
            val amt = d["price"] as? Double ?: 0.0
            val cName = d["customerName"] as? String ?: ""
            firestore.collection("earnings").add(mapOf(
                "workerId" to wid, "jobId" to jobId,
                "amount" to amt, "customerName" to cName,
                "date" to System.currentTimeMillis()
            )).await()
        }
    }

    // ── Notifications ──

    fun getNotificationsFlow(userId: String): Flow<List<Notification>> = collectionFlow(
        firestore.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING),
        ::docToNotification
    )

    suspend fun markNotificationRead(notificationId: String) {
        firestore.collection("notifications").document(notificationId)
            .update("read", true).await()
    }

    // ── Earnings ──

    fun getEarningsFlow(workerId: String): Flow<List<Earning>> = collectionFlow(
        firestore.collection("earnings")
            .whereEqualTo("workerId", workerId)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING),
        ::docToEarning
    )
}
