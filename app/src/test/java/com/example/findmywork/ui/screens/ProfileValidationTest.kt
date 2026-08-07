package com.example.findmywork.ui.screens

import com.example.findmywork.data.model.WorkerProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileValidationTest {

    @Test
    fun name_blankOrShort_isInvalid() {
        assertEquals("Enter full name", validateName(""))
        assertEquals("Enter full name", validateName("   "))
        assertEquals("Name must be at least 3 characters", validateName("Ra"))
        assertNull(validateName("Rajesh"))
    }

    @Test
    fun phone_mustBeTenDigits() {
        assertEquals("Enter valid 10-digit number", validatePhone("12345"))
        assertEquals("Enter valid 10-digit number", validatePhone("12345678901"))
        assertEquals("Enter valid 10-digit number", validatePhone("123456789a"))
        assertNull(validatePhone("9876543210"))
    }

    @Test
    fun email_isOptionalButValidWhenPresent() {
        assertNull(validateEmail(""))
        assertNull(validateEmail("rajesh@example.com"))
        assertEquals("Enter valid email", validateEmail("not-an-email"))
        assertEquals("Enter valid email", validateEmail("rajesh@"))
    }

    @Test
    fun age_mustBeBetween18And80() {
        assertNull(validateAge(""))
        assertNull(validateAge("30"))
        assertEquals("Age must be 18–80", validateAge("17"))
        assertEquals("Age must be 18–80", validateAge("81"))
        assertEquals("Age must be 18–80", validateAge("abc"))
    }

    @Test
    fun hourlyRate_mustBePositive() {
        assertEquals("Enter hourly rate", validateHourlyRate(""))
        assertEquals("Enter hourly rate", validateHourlyRate("0"))
        assertEquals("Enter hourly rate", validateHourlyRate("abc"))
        assertNull(validateHourlyRate("300"))
    }

    @Test
    fun upi_matchesExpectedPattern() {
        assertNull(validateUpi(""))
        assertNull(validateUpi("rajesh@okaxis"))
        assertNull(validateUpi("rajesh.kumar@paytm"))
        assertNull(validateUpi("rajesh-1@ybl"))
        assertEquals("Enter valid UPI ID", validateUpi("rajesh@okaxis1"))
        assertEquals("Enter valid UPI ID", validateUpi("notaupi"))
        assertEquals("Enter valid UPI ID", validateUpi("upi@"))
    }

    @Test
    fun radius_mustBeWithinOneToFifty() {
        assertNull(validateRadius(1))
        assertNull(validateRadius(50))
        assertNull(validateRadius(15))
        assertEquals("Radius 1–50 km", validateRadius(0))
        assertEquals("Radius 1–50 km", validateRadius(51))
    }

    @Test
    fun profile_validWhenAllRequiredFieldsPass() {
        val valid = WorkerProfile(
            name = "Rajesh Kumar",
            phone = "9876543210",
            email = "rajesh@example.com",
            age = 30,
            hourlyRate = 300.0,
            upiId = "rajesh@okaxis",
            workingRadiusKm = 15
        )
        assertTrue(isWorkerProfileValid(valid))
        assertTrue(validateWorkerProfile(valid).isEmpty())
    }

    @Test
    fun profile_invalidWhenRequiredFieldMissing() {
        val invalid = WorkerProfile(
            name = "Ra",
            phone = "123",
            age = 10,
            hourlyRate = 0.0
        )
        assertFalse(isWorkerProfileValid(invalid))
        val messages = validateWorkerProfile(invalid)
        assertTrue(messages.any { it.contains("name", ignoreCase = true) })
        assertTrue(messages.any { it.contains("10-digit", ignoreCase = true) })
        assertTrue(messages.any { it.contains("18–80", ignoreCase = true) })
        assertTrue(messages.any { it.contains("hourly", ignoreCase = true) })
    }
}
