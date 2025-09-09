package com.example.iwanttobelieve.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iwanttobelieve.data.repository.AuthRepository
import com.example.iwanttobelieve.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val authState: StateFlow<Resource<Unit>> = _authState

    fun isUserLoggedIn(): Boolean = repository.getCurrentUser() != null

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = Resource.Loading
            val result = repository.signIn(email, password)
            result.onSuccess {
                _authState.value = Resource.Success(Unit)
            }.onFailure {
                _authState.value = Resource.Error(it.message ?: "Erro desconhecido")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = Resource.Loading
            val result = repository.signUp(name, email, password)
            result.onSuccess {
                _authState.value = Resource.Success(Unit)
            }.onFailure {
                _authState.value = Resource.Error(it.message ?: "Erro desconhecido")
            }
        }
    }
}