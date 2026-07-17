package com.example.leafly.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.leafly.Repo.UserRepo
import com.example.leafly.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(val repo: UserRepo) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _users = MutableStateFlow<UserModel?>(null)
    val users: StateFlow<UserModel?> = _users.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserModel?>>(emptyList())
    val allUsers: StateFlow<List<UserModel?>> = _allUsers.asStateFlow()

    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.login(email, password)
            if (result.isSuccess) {
                callback(true, result.getOrDefault("Login Successful"))
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.forgetPassword(email)
            if (result.isSuccess) {
                callback(true, result.getOrDefault("Reset link sent"))
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Failed to send reset email")
            }
        }
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.logout()
            callback(result.isSuccess, if (result.isSuccess) "Logout Successful" else "Logout failed")
        }
    }

    fun getUserById(id: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repo.getUserById(id)
            _users.value = result.getOrNull()
            _loading.value = false
        }
    }

    fun getAllUser() {
        viewModelScope.launch {
            _loading.value = true
            val result = repo.getAllUser()
            _allUsers.value = result.getOrDefault(emptyList())
            _loading.value = false
        }
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.register(email, password)
            if (result.isSuccess) {
                callback(true, "Registration successful", result.getOrDefault(""))
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Registration failed", "")
            }
        }
    }

    fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.addUser(id, model)
            if (result.isSuccess) {
                callback(true, result.getOrDefault("User added"))
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Failed to add user")
            }
        }
    }

    fun editProfile(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.editProfile(id, model)
            if (result.isSuccess) {
                callback(true, result.getOrDefault("Profile updated"))
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Failed to update profile")
            }
        }
    }

    fun deleteUser(id: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repo.deleteUser(id)
            if (result.isSuccess) {
                callback(true, result.getOrDefault("Account deleted"))
            } else {
                callback(false, result.exceptionOrNull()?.message ?: "Failed to delete account")
            }
        }
    }
}

class UserViewModelFactory(private val repo: UserRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(repo) as T
    }
}
