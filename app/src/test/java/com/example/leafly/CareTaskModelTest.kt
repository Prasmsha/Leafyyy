package com.example.leafly

import com.example.leafly.model.CareTaskModel
import org.junit.Assert.*
import org.junit.Test

class CareTaskModelTest {

    @Test
    fun `task is not done by default`() {
        val task = CareTaskModel(title = "Water plant")
        assertFalse(task.done)
    }

    @Test
    fun `task can be marked done`() {
        val task = CareTaskModel(title = "Water plant", done = true)
        assertTrue(task.done)
    }

    @Test
    fun `task toMap contains done field`() {
        val task = CareTaskModel(
            id = "t1", plantId = "p1", userId = "u1",
            title = "Water", dueDate = "2026-07-17", done = false
        )
        val map = task.toMap()
        assertTrue(map.containsKey("done"))
        assertTrue(map.containsKey("title"))
        assertTrue(map.containsKey("dueDate"))
    }

    @Test
    fun `task title not empty`() {
        val task = CareTaskModel(title = "Fertilize")
        assertTrue(task.title.isNotEmpty())
    }
}
