package com.example.iwanttobelieve.ui.screens.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iwanttobelieve.data.repository.PostRepository
import com.example.iwanttobelieve.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreatePostViewModel : ViewModel() {
    private val postRepository = PostRepository()

    private val _postCreationState = MutableStateFlow<Resource<Unit>>(Resource.Idle)

    val postCreationState: StateFlow<Resource<Unit>> = _postCreationState

    fun createPost(description: String) {
        viewModelScope.launch {
            _postCreationState.value = Resource.Loading
            val result = postRepository.createPost(description)
            result.onSuccess {
                _postCreationState.value = Resource.Success(Unit)
            }.onFailure {
                _postCreationState.value = Resource.Error(it.message ?: "Erro ao criar post.")
            }
        }
    }
}