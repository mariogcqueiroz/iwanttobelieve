package com.example.iwanttobelieve.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iwanttobelieve.data.model.Post
import com.example.iwanttobelieve.data.model.User
import com.example.iwanttobelieve.data.repository.AuthRepository
import com.example.iwanttobelieve.data.repository.PostRepository
import com.example.iwanttobelieve.data.repository.ProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val postRepository: PostRepository = PostRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    init {
        loadUserProfile()
        loadUserPosts()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _user.value = profileRepository.getUserProfile()
        }
    }

    fun loadUserPosts() {
        viewModelScope.launch {
            profileRepository.getUserProfile()?.let { currentUser ->
                postRepository.getPostsByUser(currentUser.uid).collect { posts ->
                    _posts.value = posts
                }
            }
        }
    }
    fun updateProfile(name: String, email: String?, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = profileRepository.updateUserProfile(name, email)
            if (result.isSuccess) {
                loadUserPosts() // recarrega o perfil atualizado
            }
            onResult(result)
        }
    }
    fun toggleLike(postId: String) {
        viewModelScope.launch {
            postRepository.toggleLike(postId)
        }
    }

    fun addComment(postId: String, text: String) {
        viewModelScope.launch {
            postRepository.addComment(postId, text)
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
        }
    }

    fun logout() {
        authRepository.signOut()
    }
}
