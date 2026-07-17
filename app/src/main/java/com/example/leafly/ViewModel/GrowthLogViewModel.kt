package com.example.leafly.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.leafly.Repo.GrowthLogRepo
import com.example.leafly.model.GrowthLogModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GrowthLogViewModel(val repo: GrowthLogRepo) : ViewModel() {

    private val _logs = MutableStateFlow<List<GrowthLogModel>>(emptyList())
    val logs: StateFlow<List<GrowthLogModel>> = _logs.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun addLog(model: GrowthLogModel, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.addLog(model)
            if (result.isSuccess) {
                callback(true, result.getOrDefault("Log added"))
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Failed to add log")
            }
        }
    }

    fun getLogsByPlant(plantId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repo.getLogsByPlant(plantId)
            _logs.value = result.getOrDefault(emptyList())
            _loading.value = false
        }
    }

    fun deleteLog(logId: String, plantId: String) {
        viewModelScope.launch {
            val result = repo.deleteLog(logId)
            if (result.isSuccess) {
                getLogsByPlant(plantId)
            }
        }
    }
}

class GrowthLogViewModelFactory(private val repo: GrowthLogRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GrowthLogViewModel(repo) as T
    }
}
