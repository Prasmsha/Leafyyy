package com.example.leafly.model

data class GrowthLogModel(
    val id: String = "",
    val plantId: String = "",
    val userId: String = "",
    val note: String = "",
    val date: String = "",
    val photoUrl: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "plantId" to plantId,
            "userId" to userId,
            "note" to note,
            "date" to date,
            "photoUrl" to photoUrl
        )
    }
}