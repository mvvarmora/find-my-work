package com.example.findmywork.data.repository

import com.example.findmywork.data.model.Earning
import com.example.findmywork.data.model.Job
import com.example.findmywork.data.model.Notification
import com.example.findmywork.data.model.Review
import com.example.findmywork.data.model.ServiceCategory
import com.example.findmywork.data.model.Worker
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.example.findmywork.data.COLLECTION_CATEGORIES
import com.example.findmywork.data.COLLECTION_EARNINGS
import com.example.findmywork.data.COLLECTION_JOBS
import com.example.findmywork.data.COLLECTION_NOTIFICATIONS
import com.example.findmywork.data.COLLECTION_WORKERS
import com.example.findmywork.data.Fields
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
            if (error != null) {
                android.util.Log.e("FirestoreRepo", "collectionFlow error: ${error.message}", error)
                return@addSnapshotListener
            }
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
            age = (data["age"] as? Long)?.toInt() ?: 0,
            gender = data["gender"] as? String ?: "",
            categoryIds = (data["categoryIds"] as? List<String>) ?: emptyList(),
            experienceYears = (data["experienceYears"] as? Long)?.toInt()
                ?: (data["experience"] as? Long)?.toInt() ?: 0,
            description = data["description"] as? String ?: "",
            pricing = data["pricing"] as? Double ?: 0.0,
            serviceRadius = data["serviceRadius"] as? Double ?: 10.0,
            workingRadiusKm = (data["workingRadiusKm"] as? Long)?.toInt() ?: 10,
            isOnline = data["isOnline"] as? Boolean ?: false,
            ratingSum = data["ratingSum"] as? Double
                ?: ((data["rating"] as? Number)?.toDouble() ?: 0.0),
            ratingCount = (data["ratingCount"] as? Long)?.toInt() ?: 0,
            totalJobs = (data["totalJobs"] as? Long)?.toInt() ?: 0,
            completionRate = (data["completionRate"] as? Number)?.toFloat() ?: 0f,
            totalEarnings = data["totalEarnings"] as? Double ?: 0.0,
            documentsVerified = data["documentsVerified"] as? Boolean ?: false,
            bankAccount = data["bankAccount"] as? String ?: "",
            upiId = data["upiId"] as? String ?: "",
            preferredPayout = data["preferredPayout"] as? String ?: "",
            city = data["city"] as? String ?: "",
            skills = (data["skills"] as? List<String>) ?: emptyList(),
            status = data["status"] as? String ?: "PENDING",
            worksBeforeAfter = (data["worksBeforeAfter"] as? List<String>) ?: emptyList(),
            rejectionReason = data["rejectionReason"] as? String,
            active = data["active"] as? Boolean ?: true,
            createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
            updatedAt = data["updatedAt"] as? Long ?: System.currentTimeMillis()
        )
    }

    private fun docToJob(id: String, data: Map<String, Any>): Job {
        return Job(
            id = id,
            customerId = data["customerId"] as? String ?: "",
            customerName = data["customerName"] as? String ?: "",
            customerPhone = data["customerPhone"] as? String ?: "",
            customerPhoto = data["customerPhoto"] as? String ?: "",
            workerId = data["workerId"] as? String ?: "",
            workerName = data["workerName"] as? String ?: "",
            workerPhone = data["workerPhone"] as? String ?: "",
            workerRating = data["workerRating"] as? Double,
            flatNo = data["flatNo"] as? String ?: data["address"] as? String ?: "",
            societyName = data["societyName"] as? String ?: "",
            landmark = data["landmark"] as? String ?: "",
            pinCode = data["pinCode"] as? String ?: "",
            city = data["city"] as? String ?: "",
            categoryId = data["categoryId"] as? String ?: "",
            categoryName = data["categoryName"] as? String
                ?: data["service"] as? String ?: "",
            subServiceId = data["subServiceId"] as? String ?: "",
            subServiceName = data["subServiceName"] as? String
                ?: data["service"] as? String ?: "",
            basePrice = data["basePrice"] as? Double
                ?: data["price"] as? Double ?: 0.0,
            platformFee = data["platformFee"] as? Double ?: 0.0,
            gstAmount = data["gstAmount"] as? Double ?: 0.0,
            discountAmount = data["discountAmount"] as? Double ?: 0.0,
            totalAmount = data["totalAmount"] as? Double ?: 0.0,
            bookingDate = data["bookingDate"] as? String ?: "",
            timeSlot = data["timeSlot"] as? String ?: "",
            specialInstructions = data["specialInstructions"] as? String
                ?: data["description"] as? String ?: "",
            photoPath = data["photoPath"] as? String ?: "",
            status = data["status"] as? String ?: "PENDING",
            cancelledBy = data["cancelledBy"] as? String,
            rating = (data["rating"] as? Long)?.toInt()
                ?: (data["rating"] as? Number)?.toInt() ?: 0,
            review = data["review"] as? String ?: "",
            workPhotoBefore = data["workPhotoBefore"] as? String ?: "",
            workPhotoAfter = data["workPhotoAfter"] as? String ?: "",
            timestamp = data["timestamp"] as? Long ?: System.currentTimeMillis(),
            createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
            updatedAt = data["updatedAt"] as? Long ?: System.currentTimeMillis(),
            arrivedAt = data["arrivedAt"] as? Long ?: 0L,
            startedAt = data["startedAt"] as? Long ?: 0L,
            completedAt = data["completedAt"] as? Long ?: 0L,
            acceptedAt = data["acceptedAt"] as? Long ?: 0L,
            cancelledAt = data["cancelledAt"] as? Long ?: 0L,
            disputeReason = data["disputeReason"] as? String,
            paymentStatus = data["paymentStatus"] as? String ?: "PENDING",
            paymentMethod = data["paymentMethod"] as? String ?: "",
            geohash = data["geohash"] as? String ?: "",
            distance = data["distance"] as? Double ?: 0.0,
            active = data["active"] as? Boolean ?: true
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
        documentFlow("$COLLECTION_WORKERS/$workerId", ::docToWorker)

    suspend fun saveWorkerProfile(workerId: String, data: Map<String, Any>) {
        firestore.collection(COLLECTION_WORKERS).document(workerId)
            .set(data, SetOptions.merge()).await()
    }

    suspend fun updateWorkerField(workerId: String, field: String, value: Any) {
        firestore.collection(COLLECTION_WORKERS).document(workerId)
            .update(field, value).await()
    }

    // ── Jobs ──

    fun getAvailableJobsFlow(): Flow<List<Job>> = collectionFlow(
        firestore.collection(COLLECTION_JOBS)
            .whereEqualTo(Fields.STATUS, "PENDING"),
        ::docToJob
    )

    fun getActiveJobsFlow(workerId: String): Flow<List<Job>> = collectionFlow(
        firestore.collection(COLLECTION_JOBS)
            .whereEqualTo(Fields.WORKER_ID, workerId)
            .whereIn(Fields.STATUS, listOf("ACCEPTED", "ON_THE_WAY", "ARRIVED", "STARTED")),
        ::docToJob
    )

    fun getCompletedJobsFlow(workerId: String): Flow<List<Job>> = collectionFlow(
        firestore.collection(COLLECTION_JOBS)
            .whereEqualTo(Fields.WORKER_ID, workerId)
            .whereIn(Fields.STATUS, listOf("COMPLETED", "RATED")),
        ::docToJob
    )

    fun getJobByIdFlow(jobId: String): Flow<Job?> =
        documentFlow("$COLLECTION_JOBS/$jobId", ::docToJob)

    suspend fun acceptJob(jobId: String, workerId: String) {
        firestore.collection(COLLECTION_JOBS).document(jobId).update(
            mapOf(
                Fields.WORKER_ID to workerId,
                Fields.STATUS to "ACCEPTED",
                Fields.ACCEPTED_AT to System.currentTimeMillis(),
                Fields.UPDATED_AT to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun updateJobStatus(jobId: String, status: String) {
        val updates = mutableMapOf<String, Any>(
            Fields.STATUS to status,
            Fields.UPDATED_AT to System.currentTimeMillis()
        )
        if (status == "COMPLETED") {
            updates[Fields.COMPLETED_AT] = System.currentTimeMillis()
        }
        firestore.collection(COLLECTION_JOBS).document(jobId).update(updates).await()

        if (status == "COMPLETED") {
            val snap = firestore.collection(COLLECTION_JOBS).document(jobId).get().await()
            val d = snap.data ?: return
            val wid = d[Fields.WORKER_ID] as? String ?: return
            val amt = d["totalAmount"] as? Double ?: d["basePrice"] as? Double ?: d["price"] as? Double ?: 0.0
            val cName = d["customerName"] as? String ?: ""
            firestore.collection(COLLECTION_EARNINGS).add(mapOf(
                Fields.WORKER_ID to wid, "jobId" to jobId,
                "amount" to amt, "customerName" to cName,
                "date" to System.currentTimeMillis()
            )).await()
        }
    }

    // ── Notifications ──

    fun getNotificationsFlow(userId: String): Flow<List<Notification>> = collectionFlow(
        firestore.collection(COLLECTION_NOTIFICATIONS)
            .whereEqualTo("userId", userId)
            .orderBy(Fields.CREATED_AT, com.google.firebase.firestore.Query.Direction.DESCENDING),
        ::docToNotification
    )

    suspend fun markNotificationRead(notificationId: String) {
        firestore.collection(COLLECTION_NOTIFICATIONS).document(notificationId)
            .update("read", true).await()
    }

    // ── Categories ──

    suspend fun getCategories(): List<ServiceCategory> {
        val snap = firestore.collection(COLLECTION_CATEGORIES).get().await()
        return snap.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            ServiceCategory(
                id = data["id"] as? String ?: doc.id,
                name = data["name"] as? String ?: "",
                iconName = data["iconName"] as? String ?: "",
                displayOrder = (data["displayOrder"] as? Long)?.toInt() ?: 0,
                active = (data["active"] as? Boolean) ?: true
            )
        }
    }

    // ── Earnings ──

    fun getEarningsFlow(workerId: String): Flow<List<Earning>> = collectionFlow(
        firestore.collection(COLLECTION_EARNINGS)
            .whereEqualTo(Fields.WORKER_ID, workerId)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING),
        ::docToEarning
    )
}
