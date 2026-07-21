package com.example.findmywork.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {
    private val _currentUserId = MutableStateFlow<String?>("worker1")
    val currentUserIdFlow: StateFlow<String?> = _currentUserId.asStateFlow()

    fun isLoggedIn(): Boolean = _currentUserId.value != null

    fun getCurrentUserId(): String? = _currentUserId.value

    suspend fun login(email: String, password: String): Result<String> {
        _currentUserId.value = "worker1"
        return Result.success("worker1")
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Result<String> {
        _currentUserId.value = "worker1"
        return Result.success("worker1")
    }

    suspend fun logout() { _currentUserId.value = null }
}
