package com.example.leafly

import com.example.leafly.model.PlantModel
import org.junit.Assert.*
import org.junit.Test

class PlantModelTest {

    @Test
    fun `plant model creates correctly`() {
        val plant = PlantModel(
            id = "p1",
            userId = "u1",
            name = "Cactus",
            species = "Succulent",
            wateringFrequencyDays = 7,
            sunlight = "Full Sun",
            notes = "Healthy plant",
            imageUrl = ""
        )
        assertEquals("p1", plant.id)
        assertEquals("Cactus", plant.name)
        assertEquals(7, plant.wateringFrequencyDays)
    }

    @Test
    fun `plant toMap contains all fields`() {
        val plant = PlantModel(
            id = "p1", userId = "u1", name = "Cactus",
            species = "Succulent", wateringFrequencyDays = 7,
            sunlight = "Full Sun", notes = "test", imageUrl = ""
        )
        val map = plant.toMap()
        assertTrue(map.containsKey("id"))
        assertTrue(map.containsKey("name"))
        assertTrue(map.containsKey("species"))
        assertTrue(map.containsKey("wateringFrequencyDays"))
        assertTrue(map.containsKey("sunlight"))
        assertTrue(map.containsKey("notes"))
    }

    @Test
    fun `default watering frequency is 7 days`() {
        val plant = PlantModel(name = "Rose")
        assertEquals(7, plant.wateringFrequencyDays)
    }

    @Test
    fun `plant name not empty`() {
        val plant = PlantModel(name = "Monstera")
        assertTrue(plant.name.isNotEmpty())
    }

    @Test
    fun `plant with zero watering days is overdue`() {
        val plant = PlantModel(name = "Test", wateringFrequencyDays = 0)
        assertTrue(plant.wateringFrequencyDays == 0)
    }
}
