package com.example.leafly.Repo

import com.example.leafly.model.CareTaskModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CareTaskRepoImp : CareTaskRepo {
    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("careTasks")

    override suspend fun addTask(model: CareTaskModel): Result<String> = try {
        val id = ref.document().id
        val task = model.copy(id = id)
        ref.document(id).set(task).await()
        Result.success("Task added")
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTasksByPlant(plantId: String): Result<List<CareTaskModel>> = try {
        val result = ref.whereEqualTo("plantId", plantId).get().await()
        val tasks = result.toObjects(CareTaskModel::class.java)
        Result.success(tasks)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTasksByUser(userId: String): Result<List<CareTaskModel>> = try {
        val result = ref.whereEqualTo("userId", userId).get().await()
        val tasks = result.toObjects(CareTaskModel::class.java)
        Result.success(tasks)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun markTaskDone(taskId: String, done: Boolean): Result<Unit> = try {
        ref.document(taskId).update("done", done).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> = try {
        ref.document(taskId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
