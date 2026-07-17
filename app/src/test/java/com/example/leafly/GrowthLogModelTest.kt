package com.example.leafly

import com.example.leafly.model.GrowthLogModel
import org.junit.Assert.*
import org.junit.Test

class GrowthLogModelTest {

    @Test
    fun `growth log model creates correctly`() {
        val log = GrowthLogModel(
            id = "l1",
            plantId = "p1",
            userId = "u1",
            note = "Plant is growing well",
            date = "2026-07-17",
            photoUrl = ""
        )
        assertEquals("l1", log.id)
        assertEquals("Plant is growing well", log.note)
        assertEquals("2026-07-17", log.date)
    }

    @Test
    fun `growth log toMap contains all fields`() {
        val log = GrowthLogModel(
            id = "l1", plantId = "p1", userId = "u1",
            note = "Growing well", date = "2026-07-17", photoUrl = ""
        )
        val map = log.toMap()
        assertTrue(map.containsKey("id"))
        assertTrue(map.containsKey("plantId"))
        assertTrue(map.containsKey("note"))
        assertTrue(map.containsKey("date"))
    }

    @Test
    fun `default growth log has empty fields`() {
        val log = GrowthLogModel()
        assertEquals("", log.id)
        assertEquals("", log.note)
    }
}
