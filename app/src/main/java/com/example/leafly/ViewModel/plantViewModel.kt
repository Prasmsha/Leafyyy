package com.example.leafly.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.leafly.Repo.PlantRepo
import com.example.leafly.model.PlantModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlantViewModel(val repo: PlantRepo) : ViewModel() {

    private val _plants = MutableStateFlow<List<PlantModel>>(emptyList())
    val plants: StateFlow<List<PlantModel>> = _plants.asStateFlow()

    private val _selectedPlant = MutableStateFlow<PlantModel?>(null)
    val selectedPlant: StateFlow<PlantModel?> = _selectedPlant.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun addPlant(model: PlantModel, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.addPlant(model)
            if (result.isSuccess) {
                callback(true, result.getOrDefault("Plant added successfully"))
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Failed to add plant")
            }
        }
    }

    fun getPlantsByUser(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repo.getPlantsByUser(userId)
            _plants.value = result.getOrDefault(emptyList())
            _loading.value = false
        }
    }

    fun getPlantById(plantId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repo.getPlantById(plantId)
            _selectedPlant.value = result.getOrNull()
            _loading.value = false
        }
    }

    fun updatePlant(plantId: String, model: PlantModel, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.updatePlant(plantId, model)
            if (result.isSuccess) {
                callback(true, "Plant updated")
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Failed to update plant")
            }
        }
    }

    fun deletePlant(plantId: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.deletePlant(plantId)
            if (result.isSuccess) {
                callback(true, "Plant deleted")
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Failed to delete plant")
            }
        }
    }
}

class PlantViewModelFactory(private val repo: PlantRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlantViewModel(repo) as T
    }
}
