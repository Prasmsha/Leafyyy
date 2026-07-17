package com.example.leafly.Repo

import com.example.leafly.model.CareTaskModel

interface CareTaskRepo {
    suspend fun addTask(model: CareTaskModel): Result<String>
    suspend fun getTasksByPlant(plantId: String): Result<List<CareTaskModel>>
    suspend fun getTasksByUser(userId: String): Result<List<CareTaskModel>>
    suspend fun markTaskDone(taskId: String, done: Boolean): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>
}