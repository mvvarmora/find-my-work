package com.example.findmywork.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkerProfileTest {

    @Test
    fun roundTrip_preservesAllFields() {
        val original = WorkerProfile(
            name = "Rajesh Kumar",
            phone = "9876543210",
            email = "rajesh@example.com",
            age = 30,
            gender = "Male",
            city = "Rajkot",
            experienceYears = 5,
            bio = "Expert plumber",
            profilePhotoUrl = "https://example.com/p.jpg",
            skills = listOf("Plumbing", "Pipe fitting"),
            categoryIds = listOf("plumbing"),
            workingRadiusKm = 15,
            hourlyRate = 300.0,
            monthlyRate = 12000.0,
            dailyRate = 1800.0,
            perService = listOf(ServicePricing(id = "p1", name = "Tap repair", rate = 150.0)),
            upiId = "rajesh@okaxis",
            bankHolderName = "Rajesh Kumar",
            bankAccountNumber = "123456789012",
            bankIfsc = "HDFC0001234",
            availableAfterHours = true,
            packages = listOf(
                ServicePackage(id = "pkg1", name = "Deep Clean", description = "2BHK", price = 999.0, validityDays = 30)
            ),
            offers = listOf(ServiceOffer(id = "off1", title = "First booking", discountPercent = 10, description = "10% off")),
            documentsVerified = true,
            isOnline = true,
            rating = 4.8,
            totalJobs = 24
        )

        val restored = WorkerProfile.fromMap(original.toMap())

        assertEquals(original, restored)
    }

    @Test
    fun fromMap_readsLegacyWorkerFields() {
        val legacy = mapOf(
            "name" to "Old Worker",
            "phone" to "9000000000",
            "email" to "old@example.com",
            "age" to 40L,
            "gender" to "Female",
            "city" to "Mumbai",
            "experienceYears" to 8L,
            "description" to "Legacy bio",
            "pricing" to 250.0,
            "serviceRadius" to 20.0,
            "photo" to "https://legacy/p.jpg",
            "bankAccount" to "999988887777",
            "upiId" to "old@paytm",
            "categoryIds" to listOf("cleaning"),
            "skills" to listOf("Cleaning"),
            "documentsVerified" to true,
            "ratingSum" to 45.0,
            "ratingCount" to 10,
            "totalJobs" to 3
        )

        val profile = WorkerProfile.fromMap(legacy)

        assertEquals("Old Worker", profile.name)
        assertEquals(40, profile.age)
        assertEquals(8, profile.experienceYears)
        assertEquals("Legacy bio", profile.bio)
        assertEquals(250.0, profile.hourlyRate, 0.001)
        assertEquals(20, profile.workingRadiusKm)
        assertEquals("https://legacy/p.jpg", profile.profilePhotoUrl)
        assertEquals("999988887777", profile.bankAccountNumber)
        assertEquals("old@paytm", profile.upiId)
        assertEquals(listOf("cleaning"), profile.categoryIds)
        assertEquals(listOf("Cleaning"), profile.skills)
        assertTrue(profile.documentsVerified)
    }

    @Test
    fun toMap_writesLegacyFieldsForCustomerApp() {
        val profile = WorkerProfile(
            name = "New Worker",
            hourlyRate = 350.0,
            workingRadiusKm = 12,
            bio = "Hello",
            profilePhotoUrl = "https://p.jpg",
            bankAccountNumber = "111122223333"
        )

        val map = profile.toMap()

        assertEquals("New Worker", map["name"])
        assertEquals(350.0, map["pricing"] as Double, 0.001)
        assertEquals(12.0, map["serviceRadius"] as Double, 0.001)
        assertEquals("Hello", map["description"])
        assertEquals("https://p.jpg", map["photo"])
        assertEquals("111122223333", map["bankAccount"])
    }

    @Test
    fun emptyMap_returnsDefaults() {
        val profile = WorkerProfile.fromMap(emptyMap())

        assertEquals("", profile.name)
        assertEquals(0, profile.age)
        assertEquals(10, profile.workingRadiusKm)
        assertTrue(profile.packages.isEmpty())
        assertTrue(profile.offers.isEmpty())
        assertTrue(profile.perService.isEmpty())
    }

    @Test
    fun nestedLists_surviveRoundTrip() {
        val profile = WorkerProfile(
            packages = listOf(
                ServicePackage(name = "A", price = 100.0),
                ServicePackage(name = "B", price = 200.0)
            ),
            offers = listOf(ServiceOffer(title = "Deal", discountPercent = 15)),
            perService = listOf(ServicePricing(name = "X", rate = 50.0))
        )

        val restored = WorkerProfile.fromMap(profile.toMap())

        assertEquals(2, restored.packages.size)
        assertEquals(1, restored.offers.size)
        assertEquals(1, restored.perService.size)
        assertEquals("A", restored.packages[0].name)
        assertEquals(15, restored.offers[0].discountPercent)
        assertEquals(50.0, restored.perService[0].rate, 0.001)
    }
}
