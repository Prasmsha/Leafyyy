package com.example.leafly

import com.example.leafly.model.UserModel
import org.junit.Assert.*
import org.junit.Test

class UserModelTest {

    @Test
    fun `user model creates correctly`() {
        val user = UserModel(
            id = "123",
            name = "Muna Dahal",
            email = "muna@gmail.com",
            contact = "9815962767",
            address = "Jhapa",
            avatarUrl = ""
        )
        assertEquals("123", user.id)
        assertEquals("Muna Dahal", user.name)
        assertEquals("muna@gmail.com", user.email)
    }

    @Test
    fun `user toMap contains all fields`() {
        val user = UserModel(
            id = "123",
            name = "Muna Dahal",
            email = "muna@gmail.com",
            contact = "9815962767",
            address = "Jhapa",
            avatarUrl = ""
        )
        val map = user.toMap()
        assertTrue(map.containsKey("id"))
        assertTrue(map.containsKey("name"))
        assertTrue(map.containsKey("email"))
        assertTrue(map.containsKey("contact"))
        assertTrue(map.containsKey("address"))
        assertTrue(map.containsKey("avatarUrl"))
    }

    @Test
    fun `user toMap does not contain password`() {
        val user = UserModel(id = "1", name = "Test", email = "test@test.com")
        val map = user.toMap()
        assertFalse(map.containsKey("password"))
    }

    @Test
    fun `default user has empty fields`() {
        val user = UserModel()
        assertEquals("", user.id)
        assertEquals("", user.name)
        assertEquals("", user.email)
    }
}
