package com.example.findmywork.data.repository

import com.example.findmywork.data.model.Earning
import com.example.findmywork.data.model.Job
import com.example.findmywork.data.model.Notification
import com.example.findmywork.data.model.Review
import com.example.findmywork.data.model.ServiceCategory
import com.example.findmywork.data.model.Worker
import com.example.findmywork.data.model.WorkerProfile
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
import com.example.findmywork.data.COLLECTION_REVIEWS
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

    fun getWorkerFlow(workerId: String): Flow<Worker?> = callbackFlow {
        val sampleWorker = Worker(
            id = workerId,
            name = "Rajesh Varmora",
            email = "rajesh.varmora@example.com",
            phone = "+91 98765 43210",
            categoryIds = listOf("electrician"),
            experienceYears = 7,
            city = "Ahmedabad",
            isOnline = true,
            ratingSum = 695.8,
            ratingCount = 142,
            totalJobs = 142,
            totalEarnings = 1450.0,
            active = true
        )
        trySend(sampleWorker)
        val docRef = firestore.document("$COLLECTION_WORKERS/$workerId")
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) { return@addSnapshotListener }
            if (snapshot != null && snapshot.exists()) {
                snapshot.data?.let { trySend(docToWorker(snapshot.id, it)) }
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun saveWorkerProfile(workerId: String, data: Map<String, Any>) {
        try {
            firestore.collection(COLLECTION_WORKERS).document(workerId)
                .set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            android.util.Log.w("FirestoreRepo", "saveWorkerProfile fallback: ${e.message}")
        }
    }

    /**
     * One-shot read of the rich worker profile document, mapping legacy worker
     * fields (pricing, serviceRadius, description, ...) into the new model.
     */
    suspend fun getWorkerProfile(workerId: String): WorkerProfile? {
        if (workerId.isBlank()) return null
        return try {
            val doc = firestore.collection(COLLECTION_WORKERS).document(workerId).get().await()
            if (doc.exists()) doc.data?.let { WorkerProfile.fromMap(it) } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Writes all rich profile fields (pricing, packages, offers, payment, skills,
     * categories, ...) to the worker document using a merge so existing fields
     * such as ratings and earnings history are preserved.
     */
    suspend fun saveWorkerProfile(workerId: String, profile: WorkerProfile) {
        try {
            firestore.collection(COLLECTION_WORKERS).document(workerId)
                .set(profile.toMap(), SetOptions.merge()).await()
        } catch (e: Exception) {
            android.util.Log.w("FirestoreRepo", "saveWorkerProfile fallback: ${e.message}")
        }
    }

    suspend fun updateWorkerField(workerId: String, field: String, value: Any) {
        try {
            firestore.collection(COLLECTION_WORKERS).document(workerId)
                .update(field, value).await()
        } catch (e: Exception) {
            android.util.Log.w("FirestoreRepo", "updateWorkerField fallback: ${e.message}")
        }
    }

    // ── Jobs ──

    fun getAvailableJobsFlow(): Flow<List<Job>> = callbackFlow {
        val sampleJobs = listOf(
            Job(
                id = "job_sample_101",
                customerId = "cust_priya",
                customerName = "Priya Sharma",
                customerPhone = "+91 98250 11223",
                flatNo = "B-402, Shivalik Heights",
                landmark = "Near Iskcon Cross Road",
                city = "Ahmedabad",
                categoryName = "Electrician",
                subServiceName = "Ceiling Fan Repair & Wiring",
                totalAmount = 450.0,
                basePrice = 450.0,
                bookingDate = "Today",
                timeSlot = "03:00 PM - 05:00 PM",
                specialInstructions = "Fan is making clicking noise and regulator is loose.",
                status = "PENDING"
            ),
            Job(
                id = "job_sample_102",
                customerId = "cust_amit",
                customerName = "Amit Patel",
                customerPhone = "+91 99090 33445",
                flatNo = "A-12, Green Acres",
                landmark = "Behind SG Highway",
                city = "Ahmedabad",
                categoryName = "Electrician",
                subServiceName = "MCB Tripping Issue",
                totalAmount = 650.0,
                basePrice = 650.0,
                bookingDate = "Today",
                timeSlot = "05:30 PM - 07:00 PM",
                specialInstructions = "AC load is tripping the MCB switch intermittently.",
                status = "PENDING"
            )
        )
        trySend(sampleJobs)
        val query = firestore.collection(COLLECTION_JOBS).whereEqualTo(Fields.STATUS, "PENDING")
        val listener = query.addSnapshotListener { snapshots, error ->
            if (error != null) { return@addSnapshotListener }
            val list = snapshots?.documents?.mapNotNull { doc ->
                doc.data?.let { docToJob(doc.id, it) }
            } ?: emptyList()
            if (list.isNotEmpty()) {
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getActiveJobsFlow(workerId: String): Flow<List<Job>> = callbackFlow {
        val sampleActive = listOf(
            Job(
                id = "job_sample_101",
                customerId = "cust_priya",
                customerName = "Priya Sharma",
                customerPhone = "+91 98250 11223",
                workerId = workerId,
                workerName = "Rajesh Varmora",
                workerPhone = "+91 98765 43210",
                flatNo = "B-402, Shivalik Heights",
                landmark = "Near Iskcon Cross Road",
                city = "Ahmedabad",
                categoryName = "Electrician",
                subServiceName = "Ceiling Fan Repair & Wiring",
                totalAmount = 450.0,
                basePrice = 450.0,
                bookingDate = "Today",
                timeSlot = "03:00 PM - 05:00 PM",
                specialInstructions = "Fan is making clicking noise and regulator is loose.",
                status = "ACCEPTED"
            )
        )
        trySend(sampleActive)
        val query = firestore.collection(COLLECTION_JOBS)
            .whereEqualTo(Fields.WORKER_ID, workerId)
            .whereIn(Fields.STATUS, listOf("ACCEPTED", "ON_THE_WAY", "ARRIVED", "STARTED"))
        val listener = query.addSnapshotListener { snapshots, error ->
            if (error != null) { return@addSnapshotListener }
            val list = snapshots?.documents?.mapNotNull { doc ->
                doc.data?.let { docToJob(doc.id, it) }
            } ?: emptyList()
            if (list.isNotEmpty()) {
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getCompletedJobsFlow(workerId: String): Flow<List<Job>> = callbackFlow {
        val sampleCompleted = listOf(
            Job(
                id = "job_sample_098",
                customerId = "cust_vikram",
                customerName = "Vikram Mehta",
                customerPhone = "+91 97230 44556",
                flatNo = "C-701, Godrej Garden City",
                city = "Ahmedabad",
                categoryName = "Electrician",
                subServiceName = "Inverter Battery Setup",
                totalAmount = 850.0,
                basePrice = 850.0,
                bookingDate = "Yesterday",
                status = "COMPLETED",
                rating = 5,
                review = "Excellent and clean wiring work done on time!",
                completedAt = System.currentTimeMillis() - 86400000
            ),
            Job(
                id = "job_sample_092",
                customerId = "cust_neha",
                customerName = "Neha Joshi",
                customerPhone = "+91 98980 66778",
                flatNo = "D-204, Safal Parisar",
                city = "Ahmedabad",
                categoryName = "Electrician",
                subServiceName = "Switchboard Replacement",
                totalAmount = 350.0,
                basePrice = 350.0,
                bookingDate = "2 days ago",
                status = "COMPLETED",
                rating = 5,
                review = "Very polite and skilled technician.",
                completedAt = System.currentTimeMillis() - 172800000
            )
        )
        trySend(sampleCompleted)
        val query = firestore.collection(COLLECTION_JOBS).whereEqualTo(Fields.WORKER_ID, workerId)
        val listener = query.addSnapshotListener { snapshots, error ->
            if (error != null) { return@addSnapshotListener }
            val list = snapshots?.documents?.mapNotNull { doc ->
                doc.data?.let { docToJob(doc.id, it) }
            }?.filter { it.status == "COMPLETED" || it.status == "RATED" } ?: emptyList()
            if (list.isNotEmpty()) {
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getJobByIdFlow(jobId: String): Flow<Job?> = callbackFlow {
        val sampleJob = Job(
            id = jobId,
            customerId = "cust_priya",
            customerName = "Priya Sharma",
            customerPhone = "+91 98250 11223",
            workerId = "demo_worker_rajesh",
            workerName = "Rajesh Varmora",
            workerPhone = "+91 98765 43210",
            flatNo = "B-402, Shivalik Heights",
            landmark = "Near Iskcon Cross Road",
            city = "Ahmedabad",
            categoryName = "Electrician",
            subServiceName = "Ceiling Fan Repair & Wiring",
            totalAmount = 450.0,
            basePrice = 450.0,
            platformFee = 30.0,
            bookingDate = "Today",
            timeSlot = "03:00 PM - 05:00 PM",
            specialInstructions = "Fan is making clicking noise and regulator is loose.",
            status = "PENDING"
        )
        trySend(sampleJob)
        val docRef = firestore.document("$COLLECTION_JOBS/$jobId")
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) { return@addSnapshotListener }
            if (snapshot != null && snapshot.exists()) {
                snapshot.data?.let { trySend(docToJob(snapshot.id, it)) }
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun acceptJob(jobId: String, workerId: String) {
        try {
            firestore.collection(COLLECTION_JOBS).document(jobId).update(
                mapOf(
                    Fields.WORKER_ID to workerId,
                    Fields.STATUS to "ACCEPTED",
                    Fields.ACCEPTED_AT to System.currentTimeMillis(),
                    Fields.UPDATED_AT to System.currentTimeMillis()
                )
            ).await()
        } catch (e: Exception) {
            android.util.Log.w("FirestoreRepo", "acceptJob fallback: ${e.message}")
        }
    }

    suspend fun updateJobStatus(jobId: String, status: String) {
        try {
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
        } catch (e: Exception) {
            android.util.Log.w("FirestoreRepo", "updateJobStatus fallback: ${e.message}")
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
        try {
            firestore.collection(COLLECTION_NOTIFICATIONS).document(notificationId)
                .update("read", true).await()
        } catch (e: Exception) {
            android.util.Log.w("FirestoreRepo", "markNotificationRead fallback: ${e.message}")
        }
    }

    // ── Categories ──

    suspend fun getCategories(): List<ServiceCategory> {
        return try {
            val snap = firestore.collection(COLLECTION_CATEGORIES).get().await()
            snap.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                ServiceCategory(
                    id = data["id"] as? String ?: doc.id,
                    name = data["name"] as? String ?: "",
                    iconName = data["iconName"] as? String ?: "",
                    displayOrder = (data["displayOrder"] as? Long)?.toInt() ?: 0,
                    active = (data["active"] as? Boolean) ?: true
                )
            }
        } catch (e: Exception) {
            listOf(
                ServiceCategory("electrician", "Electrician", "flash_on", 1, true),
                ServiceCategory("plumber", "Plumber", "plumbing", 2, true),
                ServiceCategory("carpenter", "Carpenter", "carpenter", 3, true),
                ServiceCategory("painter", "Painter", "format_paint", 4, true)
            )
        }
    }

    // ── Earnings ──

    fun getEarningsFlow(workerId: String): Flow<List<Earning>> = callbackFlow {
        val sampleEarnings = listOf(
            Earning(
                workerId = workerId,
                amount = 850.0,
                jobId = "job_sample_098",
                customerName = "Vikram Mehta",
                date = System.currentTimeMillis() - 86400000
            ),
            Earning(
                workerId = workerId,
                amount = 600.0,
                jobId = "job_sample_092",
                customerName = "Neha Joshi",
                date = System.currentTimeMillis() - 172800000
            )
        )
        trySend(sampleEarnings)
        val query = firestore.collection(COLLECTION_EARNINGS).whereEqualTo(Fields.WORKER_ID, workerId)
        val listener = query.addSnapshotListener { snapshots, error ->
            if (error != null) { return@addSnapshotListener }
            val list = snapshots?.documents?.mapNotNull { doc ->
                doc.data?.let { docToEarning(doc.id, it) }
            } ?: emptyList()
            if (list.isNotEmpty()) {
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    // ── Customer Marketplace Operations ──

    private val sampleMarketplaceWorkers = listOf(
        Worker(
            id = "worker_johan",
            name = "Johan Thomas",
            phone = "+91 98251 10001",
            categoryIds = listOf("plumbing", "plumber"),
            experienceYears = 8,
            description = "Specialized in residential & commercial plumbing, leak detection, pipe replacement, and bathroom fixtures with 8+ years experience.",
            pricing = 500.0,
            city = "Rajkot",
            ratingSum = 588.0,
            ratingCount = 120,
            totalJobs = 250,
            completionRate = 0.98f,
            isOnline = true,
            documentsVerified = true,
            skills = listOf("Pipe Repair", "Leak Detection", "Bathroom Fixtures", "Water Heater")
        ),
        Worker(
            id = "worker_rendy",
            name = "Rendy Riyadi",
            phone = "+91 98251 10002",
            categoryIds = listOf("painting", "carpentry", "renovation"),
            experienceYears = 6,
            description = "Master craftsman in wall painting, wood polishing, ceiling design, and interior renovation with over 300 successful projects.",
            pricing = 250.0,
            city = "Rajkot",
            ratingSum = 432.0,
            ratingCount = 90,
            totalJobs = 327,
            completionRate = 0.99f,
            isOnline = true,
            documentsVerified = true,
            skills = listOf("Wall Painting", "Texture Coating", "Ceiling Work", "Tile Fitting")
        ),
        Worker(
            id = "worker_rajesh",
            name = "Rajesh Varmora",
            phone = "+91 98765 43210",
            categoryIds = listOf("electrical", "electrician"),
            experienceYears = 7,
            description = "Certified senior electrician. Expert in home wiring, inverter setups, circuit breakers, and household appliances repair.",
            pricing = 450.0,
            city = "Rajkot",
            ratingSum = 695.8,
            ratingCount = 142,
            totalJobs = 142,
            completionRate = 0.97f,
            isOnline = true,
            documentsVerified = true,
            skills = listOf("Wiring", "Inverter Setup", "MCB Tripping", "Fan & Lights")
        ),
        Worker(
            id = "worker_arif",
            name = "Arif Setyawan",
            phone = "+91 98251 10003",
            categoryIds = listOf("carpentry", "carpenter"),
            experienceYears = 5,
            description = "Skilled carpenter offering custom furniture assembly, modular kitchen fittings, door repairs, and lock replacement.",
            pricing = 600.0,
            city = "Rajkot",
            ratingSum = 399.5,
            ratingCount = 85,
            totalJobs = 190,
            completionRate = 0.96f,
            isOnline = true,
            documentsVerified = true,
            skills = listOf("Furniture Repair", "Modular Fitting", "Door & Window", "Lock Installation")
        ),
        Worker(
            id = "worker_priya",
            name = "Priya Patel",
            phone = "+91 98251 10004",
            categoryIds = listOf("cleaning", "home_cleaning"),
            experienceYears = 4,
            description = "Deep cleaning and sanitization specialist. Trusted by 400+ homes for kitchen, bathroom, and full apartment sparkle cleaning.",
            pricing = 399.0,
            city = "Rajkot",
            ratingSum = 1029.0,
            ratingCount = 210,
            totalJobs = 410,
            completionRate = 0.99f,
            isOnline = true,
            documentsVerified = true,
            skills = listOf("Deep Cleaning", "Kitchen Degreasing", "Bathroom Scrubbing", "Sofa Shampooing")
        ),
        Worker(
            id = "worker_handi",
            name = "Handi Santoso",
            phone = "+91 98251 10005",
            categoryIds = listOf("ac_repair", "appliance"),
            experienceYears = 6,
            description = "AC & refrigeration technician. Gas refilling, jet cleaning, cooling maintenance, and PCB diagnostic solutions.",
            pricing = 550.0,
            city = "Rajkot",
            ratingSum = 374.4,
            ratingCount = 78,
            totalJobs = 160,
            completionRate = 0.95f,
            isOnline = true,
            documentsVerified = true,
            skills = listOf("AC Jet Service", "Gas Charging", "Compressor Check", "Washing Machine")
        )
    )

    fun getAllWorkersFlow(): Flow<List<Worker>> = callbackFlow {
        trySend(sampleMarketplaceWorkers)
        val query = firestore.collection(COLLECTION_WORKERS).whereEqualTo(Fields.ACTIVE, true)
        val listener = query.addSnapshotListener { snapshots, error ->
            if (error != null) { return@addSnapshotListener }
            val list = snapshots?.documents?.mapNotNull { doc ->
                doc.data?.let { docToWorker(doc.id, it) }
            } ?: emptyList()
            if (list.isNotEmpty()) {
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getWorkersByCategoryFlow(categoryId: String): Flow<List<Worker>> = callbackFlow {
        val filteredSample = sampleMarketplaceWorkers.filter { worker ->
            categoryId.isBlank() || categoryId == "All" || worker.categoryIds.any { it.contains(categoryId, ignoreCase = true) }
        }
        trySend(filteredSample)
        val query = firestore.collection(COLLECTION_WORKERS)
        val listener = query.addSnapshotListener { snapshots, error ->
            if (error != null) { return@addSnapshotListener }
            val list = snapshots?.documents?.mapNotNull { doc ->
                doc.data?.let { docToWorker(doc.id, it) }
            }?.filter { worker ->
                categoryId.isBlank() || categoryId == "All" || worker.categoryIds.any { it.contains(categoryId, ignoreCase = true) }
            } ?: emptyList()
            if (list.isNotEmpty()) {
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getCustomerBookingsFlow(customerId: String): Flow<List<Job>> = callbackFlow {
        val sampleCustomerBookings = listOf(
            Job(
                id = "RXF-24671",
                customerId = customerId,
                customerName = "Ronen",
                customerPhone = "+91 98250 99999",
                workerId = "worker_johan",
                workerName = "Johan Thomas",
                workerPhone = "+91 98251 10001",
                workerRating = 4.9,
                flatNo = "A-304, Royal Palms",
                landmark = "Near Ring Road",
                city = "Rajkot",
                categoryId = "plumbing",
                categoryName = "Plumbing",
                subServiceName = "Pipe Leakage & Tap Replacement",
                basePrice = 500.0,
                platformFee = 40.0,
                gstAmount = 24.0,
                totalAmount = 564.0,
                bookingDate = "Today, 18 Sep",
                timeSlot = "13:00 - 15:00",
                specialInstructions = "Kitchen sink pipe is leaking under the cabinet. Please bring replacement valve.",
                status = "ON_THE_WAY"
            ),
            Job(
                id = "RXF-19042",
                customerId = customerId,
                customerName = "Ronen",
                customerPhone = "+91 98250 99999",
                workerId = "worker_rendy",
                workerName = "Rendy Riyadi",
                workerPhone = "+91 98251 10002",
                workerRating = 4.8,
                flatNo = "A-304, Royal Palms",
                landmark = "Near Ring Road",
                city = "Rajkot",
                categoryId = "painting",
                categoryName = "Painting",
                subServiceName = "Living Room Accent Wall",
                basePrice = 1200.0,
                platformFee = 60.0,
                gstAmount = 60.0,
                totalAmount = 1320.0,
                bookingDate = "Yesterday",
                timeSlot = "10:00 - 13:00",
                status = "COMPLETED",
                rating = 5,
                review = "Punctual, clean work, and excellent wall finish!"
            )
        )
        trySend(sampleCustomerBookings)

        val query = firestore.collection(COLLECTION_JOBS)
            .whereEqualTo(Fields.CUSTOMER_ID, customerId)
        val listener = query.addSnapshotListener { snapshots, error ->
            if (error != null) { return@addSnapshotListener }
            val list = snapshots?.documents?.mapNotNull { doc ->
                doc.data?.let { docToJob(doc.id, it) }
            } ?: emptyList()
            if (list.isNotEmpty()) {
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun createCustomerBooking(job: Job): String {
        return try {
            val docRef = if (job.id.isNotBlank()) {
                firestore.collection(COLLECTION_JOBS).document(job.id)
            } else {
                firestore.collection(COLLECTION_JOBS).document()
            }
            val id = docRef.id
            val data = mapOf(
                "id" to id,
                Fields.CUSTOMER_ID to job.customerId,
                "customerName" to job.customerName,
                "customerPhone" to job.customerPhone,
                "customerPhoto" to job.customerPhoto,
                Fields.WORKER_ID to (job.workerId ?: ""),
                "workerName" to (job.workerName ?: ""),
                "workerPhone" to (job.workerPhone ?: ""),
                "workerRating" to (job.workerRating ?: 4.8),
                "flatNo" to job.flatNo,
                "societyName" to job.societyName,
                "landmark" to job.landmark,
                "pinCode" to job.pinCode,
                "city" to job.city,
                "categoryId" to job.categoryId,
                "categoryName" to job.categoryName,
                "subServiceId" to job.subServiceId,
                "subServiceName" to job.subServiceName,
                "basePrice" to job.basePrice,
                "platformFee" to job.platformFee,
                "gstAmount" to job.gstAmount,
                "discountAmount" to job.discountAmount,
                "totalAmount" to job.totalAmount,
                "bookingDate" to job.bookingDate,
                "timeSlot" to job.timeSlot,
                "specialInstructions" to job.specialInstructions,
                Fields.STATUS to "PENDING",
                Fields.CREATED_AT to System.currentTimeMillis(),
                Fields.UPDATED_AT to System.currentTimeMillis()
            )
            docRef.set(data).await()
            id
        } catch (e: Exception) {
            android.util.Log.w("FirestoreRepo", "createCustomerBooking fallback: ${e.message}")
            "RXF-" + (10000..99999).random()
        }
    }

    suspend fun cancelCustomerBooking(jobId: String, reason: String) {
        try {
            firestore.collection(COLLECTION_JOBS).document(jobId).update(
                mapOf(
                    Fields.STATUS to "CANCELLED",
                    Fields.CANCELLED_BY to "CUSTOMER",
                    Fields.CANCELLED_AT to System.currentTimeMillis(),
                    Fields.DISPUTE_REASON to reason,
                    Fields.UPDATED_AT to System.currentTimeMillis()
                )
            ).await()
        } catch (e: Exception) {
            android.util.Log.w("FirestoreRepo", "cancelCustomerBooking fallback: ${e.message}")
        }
    }

    suspend fun submitReview(
        jobId: String,
        workerId: String,
        rating: Int,
        comment: String,
        customerName: String
    ) {
        try {
            firestore.collection(COLLECTION_REVIEWS).add(
                mapOf(
                    "jobId" to jobId,
                    Fields.WORKER_ID to workerId,
                    "customerName" to customerName,
                    "rating" to rating.toFloat(),
                    "comment" to comment,
                    "date" to System.currentTimeMillis()
                )
            ).await()

            firestore.collection(COLLECTION_JOBS).document(jobId).update(
                mapOf(
                    "rating" to rating,
                    "review" to comment,
                    Fields.STATUS to "RATED",
                    Fields.UPDATED_AT to System.currentTimeMillis()
                )
            ).await()
        } catch (e: Exception) {
            android.util.Log.w("FirestoreRepo", "submitReview fallback: ${e.message}")
        }
    }
}

