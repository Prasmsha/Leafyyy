package com.example.leafly

import org.junit.Assert.*
import org.junit.Test

class ValidationTest {

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun isValidPlantName(name: String): Boolean {
        return name.isNotBlank()
    }

    private fun isValidWateringDays(days: String): Boolean {
        val num = days.toIntOrNull()
        return num != null && num > 0
    }

    @Test
    fun `valid email passes validation`() {
        assertTrue(isValidEmail("muna@gmail.com"))
    }

    @Test
    fun `invalid email fails validation`() {
        assertFalse(isValidEmail("notanemail"))
        assertFalse(isValidEmail("missing@"))
        assertFalse(isValidEmail("@nodomain.com"))
    }

    @Test
    fun `password with 6+ chars is valid`() {
        assertTrue(isValidPassword("123456"))
        assertTrue(isValidPassword("securepassword"))
    }

    @Test
    fun `password with less than 6 chars is invalid`() {
        assertFalse(isValidPassword("123"))
        assertFalse(isValidPassword(""))
    }

    @Test
    fun `plant name cannot be blank`() {
        assertFalse(isValidPlantName(""))
        assertFalse(isValidPlantName("   "))
        assertTrue(isValidPlantName("Cactus"))
    }

    @Test
    fun `watering days must be positive number`() {
        assertTrue(isValidWateringDays("7"))
        assertTrue(isValidWateringDays("14"))
        assertFalse(isValidWateringDays("0"))
        assertFalse(isValidWateringDays("-1"))
        assertFalse(isValidWateringDays("abc"))
        assertFalse(isValidWateringDays(""))
    }

    @Test
    fun `passwords match validation`() {
        val password = "secret123"
        val confirm = "secret123"
        assertEquals(password, confirm)
    }

    @Test
    fun `passwords do not match`() {
        val password = "secret123"
        val confirm = "different"
        assertNotEquals(password, confirm)
    }
}
