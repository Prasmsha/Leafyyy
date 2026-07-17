package com.example.leafly.Repo

import com.example.leafly.model.GrowthLogModel

interface GrowthLogRepo {
    suspend fun addLog(model: GrowthLogModel): Result<String>
    suspend fun getLogsByPlant(plantId: String): Result<List<GrowthLogModel>>
    suspend fun deleteLog(logId: String): Result<Unit>
}