package com.example.iwanttobelieve.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iwanttobelieve.data.model.Post
import com.example.iwanttobelieve.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val postRepository: PostRepository = PostRepository()
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    init {
        viewModelScope.launch {
            postRepository.getPosts().collect { list ->
                _posts.value = list
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            postRepository.toggleLike(postId)
        }
    }
    suspend fun getPostById(postId: String): Post? {
        return postRepository.getPostById(postId)
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
        }
    }

    suspend fun editPost(postId: String, newDescription: String): Result<Unit> {
        return postRepository.editPostDescription(postId, newDescription)
    }
    fun addComment(postId: String, text: String) {
        viewModelScope.launch { postRepository.addComment(postId, text) }
    }

}
