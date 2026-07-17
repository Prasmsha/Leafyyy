package com.example.leafly.Repo

import com.example.leafly.model.PlantModel

interface PlantRepo {
    suspend fun addPlant(model: PlantModel): Result<String>
    suspend fun getPlantsByUser(userId: String): Result<List<PlantModel>>
    suspend fun getPlantById(plantId: String): Result<PlantModel?>
    suspend fun updatePlant(plantId: String, model: PlantModel): Result<Unit>
    suspend fun deletePlant(plantId: String): Result<Unit>
}