package com.example.leafly.model

data class CareTaskModel(
    val id: String = "",
    val plantId: String = "",
    val userId: String = "",
    val title: String = "",
    val dueDate: String = "",
    val done: Boolean = false
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "plantId" to plantId,
            "userId" to userId,
            "title" to title,
            "dueDate" to dueDate,
            "done" to done
        )
    }
}