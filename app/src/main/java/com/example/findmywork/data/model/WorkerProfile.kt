package com.example.findmywork.data.model

import java.util.UUID

/**
 * Rich editable profile for a worker, persisted to the `workers` Firestore document.
 *
 * The document is shared with the Find My Worker (customer) app, so `toMap()`
 * also writes the legacy flat fields the customer app understands (`pricing`,
 * `serviceRadius`, `description`, `photo`, `bankAccount`, ...).
 */
data class WorkerProfile(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val age: Int = 0,
    val gender: String = "",
    val city: String = "",
    val experienceYears: Int = 0,
    val bio: String = "",
    val profilePhotoUrl: String = "",
    val skills: List<String> = emptyList(),
    val categoryIds: List<String> = emptyList(),
    val workingRadiusKm: Int = 10,
    val hourlyRate: Double = 0.0,
    val monthlyRate: Double = 0.0,
    val dailyRate: Double = 0.0,
    val perService: List<ServicePricing> = emptyList(),
    val upiId: String = "",
    val bankHolderName: String = "",
    val bankAccountNumber: String = "",
    val bankIfsc: String = "",
    val availableAfterHours: Boolean = false,
    val packages: List<ServicePackage> = emptyList(),
    val offers: List<ServiceOffer> = emptyList(),
    val documentsVerified: Boolean = false,
    val isOnline: Boolean = false,
    val rating: Double = 0.0,
    val totalJobs: Int = 0
) {

    /** Grouped convenience views used by the UI / repository. */
    val pricing: Pricing
        get() = Pricing(hourlyRate, monthlyRate, dailyRate, perService)

    val payment: PaymentInfo
        get() = PaymentInfo(
            upiId = upiId,
            bankHolderName = bankHolderName,
            bankAccountNumber = bankAccountNumber,
            bankIfsc = bankIfsc,
            availableAfterHours = availableAfterHours
        )

    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "phone" to phone,
        "email" to email,
        "age" to age,
        "gender" to gender,
        "city" to city,
        "experienceYears" to experienceYears,
        "bio" to bio,
        "profilePhotoUrl" to profilePhotoUrl,
        "skills" to skills,
        "categoryIds" to categoryIds,
        "workingRadiusKm" to workingRadiusKm,
        "hourlyRate" to hourlyRate,
        "monthlyRate" to monthlyRate,
        "dailyRate" to dailyRate,
        "perService" to perService.map { it.toMap() },
        "upiId" to upiId,
        "bankHolderName" to bankHolderName,
        "bankAccountNumber" to bankAccountNumber,
        "bankIfsc" to bankIfsc,
        "availableAfterHours" to availableAfterHours,
        "packages" to packages.map { it.toMap() },
        "offers" to offers.map { it.toMap() },
        "documentsVerified" to documentsVerified,
        "isOnline" to isOnline,
        "rating" to rating,
        "totalJobs" to totalJobs,
        // Legacy fields read by the customer app's Worker model.
        "description" to bio,
        "pricing" to hourlyRate,
        "serviceRadius" to workingRadiusKm.toDouble(),
        "photo" to profilePhotoUrl,
        "bankAccount" to bankAccountNumber,
        "worksBeforeAfter" to if (availableAfterHours) listOf("MORNING", "EVENING") else emptyList(),
        "updatedAt" to System.currentTimeMillis()
    )

    companion object {
        /** Maps a worker Firestore document into a [WorkerProfile], including legacy fields. */
        fun fromMap(data: Map<String, Any>): WorkerProfile {
            val perService = data.mapList("perService").map { ServicePricing.fromMap(it) }
            val packages = data.mapList("packages").map { ServicePackage.fromMap(it) }
            val offers = data.mapList("offers").map { ServiceOffer.fromMap(it) }
            return WorkerProfile(
                name = data.string("name"),
                phone = data.string("phone"),
                email = data.string("email"),
                age = data.int("age"),
                gender = data.string("gender"),
                city = data.string("city"),
                experienceYears = data.int("experienceYears", data.int("experience", 0)),
                bio = data.string("bio", data.string("description")),
                profilePhotoUrl = data.string("profilePhotoUrl", data.string("photo")),
                skills = data.stringList("skills"),
                categoryIds = data.stringList("categoryIds"),
                workingRadiusKm = data.int("workingRadiusKm", (data["serviceRadius"] as? Number)?.toInt() ?: 10),
                hourlyRate = data.number("hourlyRate", data.number("pricing", 0.0)),
                monthlyRate = data.number("monthlyRate", 0.0),
                dailyRate = data.number("dailyRate", 0.0),
                perService = perService,
                upiId = data.string("upiId"),
                bankHolderName = data.string("bankHolderName"),
                bankAccountNumber = data.string("bankAccountNumber", data.string("bankAccount")),
                bankIfsc = data.string("bankIfsc"),
                availableAfterHours = data.bool("availableAfterHours"),
                packages = packages,
                offers = offers,
                documentsVerified = data.bool("documentsVerified"),
                isOnline = data.bool("isOnline"),
                rating = data.number("rating", data.number("ratingSum", 0.0)),
                totalJobs = data.int("totalJobs")
            )
        }
    }
}

/**
 * A priced, valid package a worker offers (e.g. "Deep Clean 2BHK — ₹999 / 1 month").
 */
data class ServicePackage(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val validityDays: Int = 30
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "name" to name,
        "description" to description,
        "price" to price,
        "validityDays" to validityDays
    )

    companion object {
        fun fromMap(data: Map<String, Any>): ServicePackage = ServicePackage(
            id = data.string("id"),
            name = data.string("name"),
            description = data.string("description"),
            price = data.number("price"),
            validityDays = data.int("validityDays", 30)
        )
    }
}

/**
 * A promotional offer a worker runs (e.g. "First booking 10% off").
 */
data class ServiceOffer(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val discountPercent: Int = 0,
    val description: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "discountPercent" to discountPercent,
        "description" to description
    )

    companion object {
        fun fromMap(data: Map<String, Any>): ServiceOffer = ServiceOffer(
            id = data.string("id"),
            title = data.string("title"),
            discountPercent = data.int("discountPercent"),
            description = data.string("description")
        )
    }
}

/** Per-service custom rate (e.g. "AC Service — ₹450 / visit"). */
data class ServicePricing(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val rate: Double = 0.0
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "name" to name,
        "rate" to rate
    )

    companion object {
        fun fromMap(data: Map<String, Any>): ServicePricing = ServicePricing(
            id = data.string("id"),
            name = data.string("name"),
            rate = data.number("rate")
        )
    }
}

/** Grouped pricing (hourly / monthly / daily + per-service list). */
data class Pricing(
    val hourlyRate: Double = 0.0,
    val monthlyRate: Double = 0.0,
    val dailyRate: Double = 0.0,
    val perService: List<ServicePricing> = emptyList()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "hourlyRate" to hourlyRate,
        "monthlyRate" to monthlyRate,
        "dailyRate" to dailyRate,
        "perService" to perService.map { it.toMap() }
    )

    companion object {
        fun fromMap(data: Map<String, Any>): Pricing = Pricing(
            hourlyRate = data.number("hourlyRate"),
            monthlyRate = data.number("monthlyRate"),
            dailyRate = data.number("dailyRate"),
            perService = data.mapList("perService").map { ServicePricing.fromMap(it) }
        )
    }
}

/** Grouped payment info (UPI + bank account + availability preference). */
data class PaymentInfo(
    val upiId: String = "",
    val bankHolderName: String = "",
    val bankAccountNumber: String = "",
    val bankIfsc: String = "",
    val availableAfterHours: Boolean = false
) {
    fun toMap(): Map<String, Any> = mapOf(
        "upiId" to upiId,
        "bankHolderName" to bankHolderName,
        "bankAccountNumber" to bankAccountNumber,
        "bankIfsc" to bankIfsc,
        "availableAfterHours" to availableAfterHours
    )

    companion object {
        fun fromMap(data: Map<String, Any>): PaymentInfo = PaymentInfo(
            upiId = data.string("upiId"),
            bankHolderName = data.string("bankHolderName"),
            bankAccountNumber = data.string("bankAccountNumber", data.string("bankAccount")),
            bankIfsc = data.string("bankIfsc"),
            availableAfterHours = data.bool("availableAfterHours")
        )
    }
}

// ── Small safe-read helpers ──

private fun Map<String, Any>.string(key: String, fallback: String = ""): String =
    this[key] as? String ?: fallback

private fun Map<String, Any>.int(key: String, fallback: Int = 0): Int =
    (this[key] as? Number)?.toInt() ?: fallback

private fun Map<String, Any>.number(key: String, fallback: Double = 0.0): Double =
    (this[key] as? Number)?.toDouble() ?: fallback

private fun Map<String, Any>.bool(key: String, fallback: Boolean = false): Boolean =
    this[key] as? Boolean ?: fallback

private fun Map<String, Any>.stringList(key: String): List<String> =
    (this[key] as? List<*>)?.filterIsInstance<String>() ?: emptyList()

private fun Map<String, Any>.mapList(key: String): List<Map<String, Any>> =
    (this[key] as? List<*>)?.mapNotNull { it as? Map<String, Any> } ?: emptyList()
