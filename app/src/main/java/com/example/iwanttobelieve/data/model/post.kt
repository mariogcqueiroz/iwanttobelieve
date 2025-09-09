package com.example.iwanttobelieve.data.model

import com.google.firebase.Timestamp

data class Post(
    val id: String = "",
    val authorUid: String = "",
    val authorName: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val likedBy: List<String> = emptyList(), // lista de uids que curtiram
    val comments: List<Comment> = emptyList(),
    val currentUserId: String? = null
) {
    val likesCount: Int
        get() = likedBy.size
}
data class Comment(
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)