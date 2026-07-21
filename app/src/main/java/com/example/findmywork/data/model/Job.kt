package com.example.findmywork.data.model

enum class JobStatus {
    PENDING, ACCEPTED, ON_THE_WAY, ARRIVED, STARTED, COMPLETED, RATED, CANCELLED
}

data class Job(
    val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhoto: String = "",
    val workerId: String = "",
    val service: String = "",
    val description: String = "",
    val address: String = "",
    val price: Double = 0.0,
    val distance: Double = 0.0,
    val status: JobStatus = JobStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val rating: Float = 0f,
    val review: String = ""
)

data class Worker(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photo: String = "",
    val profession: String = "",
    val experience: Int = 0,
    val description: String = "",
    val pricing: Double = 0.0,
    val serviceRadius: Double = 10.0,
    val isOnline: Boolean = false,
    val rating: Float = 0f,
    val totalJobs: Int = 0,
    val completionRate: Float = 0f,
    val totalEarnings: Double = 0.0,
    val documentsVerified: Boolean = false,
    val skills: List<String> = emptyList(),
    val reviews: List<Review> = emptyList()
)

data class Review(
    val customerName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val date: Long = System.currentTimeMillis()
)

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class Earning(
    val date: Long = System.currentTimeMillis(),
    val amount: Double = 0.0,
    val jobId: String = "",
    val customerName: String = ""
)
