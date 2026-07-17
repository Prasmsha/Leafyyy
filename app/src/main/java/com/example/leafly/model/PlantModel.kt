package com.example.leafly.model

data class PlantModel(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val species: String = "",
    val wateringFrequencyDays: Int = 7,
    val sunlight: String = "",
    val notes: String = "",
    val imageUrl: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "name" to name,
            "species" to species,
            "wateringFrequencyDays" to wateringFrequencyDays,
            "sunlight" to sunlight,
            "notes" to notes,
            "imageUrl" to imageUrl
        )
    }
}