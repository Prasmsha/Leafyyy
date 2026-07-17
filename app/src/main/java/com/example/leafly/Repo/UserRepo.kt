package com.example.leafly.Repo

import com.example.leafly.model.UserModel

interface UserRepo {
    suspend fun login(email: String, password: String): Result<String>
    suspend fun forgetPassword(email: String): Result<String>
    suspend fun getUserById(id: String): Result<UserModel?>
    suspend fun getAllUser(): Result<List<UserModel?>>
    suspend fun logout(): Result<Unit>
    suspend fun register(email: String, password: String): Result<String>
    suspend fun addUser(id: String, model: UserModel): Result<String>
    suspend fun editProfile(id: String, model: UserModel): Result<String>
    suspend fun deleteUser(id: String): Result<String>
}