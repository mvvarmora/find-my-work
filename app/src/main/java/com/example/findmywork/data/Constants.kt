package com.example.findmywork.data

/**
 * Shared constants for the Firestore bridge between Find My Worker (customer)
 * and Find My Work (worker) apps.
 * Both apps must use identical collection names and status values.
 */

// ── Firestore Collection Names ──
const val COLLECTION_JOBS = "jobs"
const val COLLECTION_WORKERS = "workers"
const val COLLECTION_USERS = "users"
const val COLLECTION_REVIEWS = "reviews"
const val COLLECTION_EARNINGS = "earnings"
const val COLLECTION_NOTIFICATIONS = "notifications"
const val COLLECTION_CATEGORIES = "categories"
const val COLLECTION_SUB_SERVICES = "sub_services"

// ── Job Status Enum ──
enum class JobStatus(val value: String) {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    ON_THE_WAY("ON_THE_WAY"),
    ARRIVED("ARRIVED"),
    STARTED("STARTED"),
    COMPLETED("COMPLETED"),
    RATED("RATED"),
    CANCELLED("CANCELLED");

    companion object {
        fun fromValue(value: String): JobStatus =
            entries.firstOrNull { it.value == value } ?: PENDING
    }
}

// ── Payment Status ──
object PaymentStatus {
    const val UNPAID = "UNPAID"
    const val PAID = "PAID"
    const val REFUND_PENDING = "REFUND_PENDING"
    const val REFUNDED = "REFUNDED"
}

// ── Worker Status ──
object WorkerStatus {
    const val PENDING = "PENDING"
    const val ACTIVE = "ACTIVE"
    const val SUSPENDED = "SUSPENDED"
    const val REJECTED = "REJECTED"
}

// ── User Roles ──
object UserRole {
    const val CUSTOMER = "CUSTOMER"
    const val WORKER = "WORKER"
    const val ADMIN = "ADMIN"
}

// ── Cancellation Actor ──
object CancelledBy {
    const val CUSTOMER = "CUSTOMER"
    const val WORKER = "WORKER"
    const val ADMIN = "ADMIN"
}

// ── Field Names (for Firestore updates) ──
object Fields {
    const val STATUS = "status"
    const val CUSTOMER_ID = "customerId"
    const val WORKER_ID = "workerId"
    const val RATING_SUM = "ratingSum"
    const val RATING_COUNT = "ratingCount"
    const val TOTAL_JOBS = "totalJobs"
    const val TOTAL_EARNINGS = "totalEarnings"
    const val ACTIVE = "active"
    const val ARRIVED_AT = "arrivedAt"
    const val STARTED_AT = "startedAt"
    const val COMPLETED_AT = "completedAt"
    const val ACCEPTED_AT = "acceptedAt"
    const val PAYMENT_STATUS = "paymentStatus"
    const val PAYMENT_METHOD = "paymentMethod"
    const val UPDATED_AT = "updatedAt"
    const val CREATED_AT = "createdAt"
    const val CANCELLED_BY = "cancelledBy"
    const val CANCELLED_AT = "cancelledAt"
    const val DISPUTE_REASON = "disputeReason"
    const val IS_ONLINE = "isOnline"
}
