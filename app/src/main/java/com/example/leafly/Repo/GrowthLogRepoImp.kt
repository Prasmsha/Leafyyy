package com.example.leafly.Repo

import com.example.leafly.model.GrowthLogModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GrowthLogRepoImp : GrowthLogRepo {
    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("growthLogs")

    override suspend fun addLog(model: GrowthLogModel): Result<String> = try {
        val id = ref.document().id
        val log = model.copy(id = id)
        ref.document(id).set(log).await()
        Result.success("Log added")
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getLogsByPlant(plantId: String): Result<List<GrowthLogModel>> = try {
        val result = ref.whereEqualTo("plantId", plantId).get().await()
        val logs = result.toObjects(GrowthLogModel::class.java).sortedByDescending { it.date }
        Result.success(logs)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteLog(logId: String): Result<Unit> = try {
        ref.document(logId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
