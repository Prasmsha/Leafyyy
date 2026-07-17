package com.example.leafly.model

data class UserModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val contact: String = "",
    val address: String = "",
    val avatarUrl: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "contact" to contact,
            "address" to address,
            "avatarUrl" to avatarUrl
        )
    }
}
