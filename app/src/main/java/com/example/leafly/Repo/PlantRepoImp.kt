package com.example.leafly.Repo

import com.example.leafly.model.PlantModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PlantRepoImp : PlantRepo {
    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("plants")

    private val realtimeDb = FirebaseDatabase.getInstance()
    private val plantsRef = realtimeDb.getReference("plants")

    override suspend fun addPlant(model: PlantModel): Result<String> = try {
        val id = ref.document().id
        val plant = model.copy(id = id)
        // Save to Firestore
        ref.document(id).set(plant).await()
        // Also save to Realtime Database
        plantsRef.child(model.userId).child(id).setValue(plant.toMap()).await()
        Result.success("Plant added successfully")
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPlantsByUser(userId: String): Result<List<PlantModel>> = try {
        // Fallback to Firestore for a simpler suspend implementation
        // If you need real-time, you'd use Flow, but for a single fetch:
        val result = ref.whereEqualTo("userId", userId).get().await()
        val plants = result.toObjects(PlantModel::class.java)
        Result.success(plants)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPlantById(plantId: String): Result<PlantModel?> = try {
        val result = ref.document(plantId).get().await()
        val plant = result.toObject(PlantModel::class.java)
        Result.success(plant)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updatePlant(plantId: String, model: PlantModel): Result<Unit> = try {
        // Update Firestore
        ref.document(plantId).update(model.toMap()).await()
        // Update Realtime Database
        plantsRef.child(model.userId).child(plantId).updateChildren(model.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deletePlant(plantId: String): Result<Unit> = try {
        // Get plant to find userId for Realtime DB path
        val result = ref.document(plantId).get().await()
        val plant = result.toObject(PlantModel::class.java)
        // Delete from Firestore
        ref.document(plantId).delete().await()
        // Delete from Realtime Database
        plant?.let {
            plantsRef.child(it.userId).child(plantId).removeValue().await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
