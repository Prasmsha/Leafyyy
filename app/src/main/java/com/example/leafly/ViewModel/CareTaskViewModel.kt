package com.example.leafly.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.leafly.Repo.CareTaskRepo
import com.example.leafly.model.CareTaskModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CareTaskViewModel(val repo: CareTaskRepo) : ViewModel() {

    private val _tasks = MutableStateFlow<List<CareTaskModel>>(emptyList())
    val tasks: StateFlow<List<CareTaskModel>> = _tasks.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun addTask(model: CareTaskModel, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.addTask(model)
            if (result.isSuccess) {
                callback(true, result.getOrDefault("Task added"))
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Failed to add task")
            }
        }
    }

    fun getTasksByPlant(plantId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repo.getTasksByPlant(plantId)
            _tasks.value = result.getOrDefault(emptyList())
            _loading.value = false
        }
    }

    fun getTasksByUser(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repo.getTasksByUser(userId)
            _tasks.value = result.getOrDefault(emptyList())
            _loading.value = false
        }
    }

    fun markTaskDone(taskId: String, done: Boolean, plantId: String) {
        viewModelScope.launch {
            val result = repo.markTaskDone(taskId, done)
            if (result.isSuccess) {
                getTasksByPlant(plantId)
            }
        }
    }

    fun deleteTask(taskId: String, plantId: String) {
        viewModelScope.launch {
            val result = repo.deleteTask(taskId)
            if (result.isSuccess) {
                getTasksByPlant(plantId)
            }
        }
    }
}

class CareTaskViewModelFactory(private val repo: CareTaskRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CareTaskViewModel(repo) as T
    }
}
