package com.example.iwanttobelieve.data.repository

import android.util.Log
import com.example.iwanttobelieve.data.model.Comment
import com.example.iwanttobelieve.data.model.Post
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class PostRepository(
    private val profileRepository: ProfileRepository = ProfileRepository()
) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Helper: converte documento para Post garantindo likedBy correto
    private fun documentToPostSafe(doc: com.google.firebase.firestore.DocumentSnapshot): Post? {
        val base = doc.toObject(Post::class.java)
        val likedByFromSnapshot = doc.get("likedBy") as? List<*>
        val likedByStrings = likedByFromSnapshot?.mapNotNull { it as? String } ?: base?.likedBy ?: emptyList()

        return if (base != null) {
            base.copy(id = doc.id, likedBy = likedByStrings)
        } else {
            // fallback: criar Post manualmente se toObject falhar
            val authorUid = doc.getString("authorUid") ?: ""
            val authorName = doc.getString("authorName") ?: ""
            val description = doc.getString("description") ?: ""
            val imageUrl = doc.getString("imageUrl") ?: ""
            val timestamp = doc.get("timestamp") as? Timestamp ?: Timestamp.now()
            Post(
                id = doc.id,
                authorUid = authorUid,
                authorName = authorName,
                description = description,
                imageUrl = imageUrl,
                timestamp = timestamp,
                likedBy = likedByStrings
            )
        }
    }
    suspend fun addComment(postId: String, text: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val firebaseUser = auth.currentUser ?: return@withContext Result.failure(Exception("Usuário não logado"))
            val userProfile = profileRepository.getUserProfile() ?: return@withContext Result.failure(Exception("Perfil não encontrado"))

            val comment = Comment(
                userId = userProfile.uid,
                userName = userProfile.name,
                text = text
            )

            db.collection("posts").document(postId)
                .update("comments", FieldValue.arrayUnion(comment))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPosts(): Flow<List<Post>> = callbackFlow {
        val listener = db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("PostRepository", "Listen failed.", e)
                    close(e)
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { documentToPostSafe(it) } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    fun getPostsByUser(userId: String): Flow<List<Post>> = callbackFlow {
        val listener = db.collection("posts")
            .whereEqualTo("authorUid", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("PostRepository", "getPostsByUser listen failed.", e)
                    close(e)
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { documentToPostSafe(it) } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createPost(description: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val firebaseUser = auth.currentUser
                ?: return@withContext Result.failure(Exception("Usuário não logado."))

            val userProfile = profileRepository.getUserProfile()
                ?: return@withContext Result.failure(Exception("Perfil do usuário não encontrado."))

            val staticImageUrl = "https://picsum.photos/seed/picsum/400/300"
            val post = Post(
                authorUid = userProfile.uid,
                authorName = userProfile.name,
                description = description,
                imageUrl = staticImageUrl
            )

            val result = withTimeoutOrNull(5000) {
                db.collection("posts").add(post).await()
            }
            if (result == null) {
                return@withContext Result.failure(Exception("Timeout ao criar post."))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PostRepository", "createPost failed", e)
            Result.failure(e)
        }
    }

    // Toggle like com arrayUnion/arrayRemove dentro de transação
    suspend fun toggleLike(postId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val firebaseUser = auth.currentUser ?: return@withContext Result.failure(Exception("Usuário não logado."))

            val postRef = db.collection("posts").document(postId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val current = snapshot.get("likedBy") as? List<*> ?: emptyList<Any>()
                val contains = current.mapNotNull { it as? String }.contains(firebaseUser.uid)

                if (contains) {
                    transaction.update(postRef, "likedBy", FieldValue.arrayRemove(firebaseUser.uid))
                } else {
                    transaction.update(postRef, "likedBy", FieldValue.arrayUnion(firebaseUser.uid))
                }
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PostRepository", "toggleLike failed", e)
            Result.failure(e)
        }
    }



    suspend fun deletePost(postId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val firebaseUser = auth.currentUser ?: return@withContext Result.failure(Exception("Usuário não logado."))
            val postRef = db.collection("posts").document(postId)

            val snapshot = postRef.get().await()
            val post = snapshot.toObject(Post::class.java) ?: return@withContext Result.failure(Exception("Post não encontrado."))

            if (post.authorUid != firebaseUser.uid) {
                return@withContext Result.failure(Exception("Apenas o autor pode excluir o post."))
            }

            postRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PostRepository", "deletePost failed", e)
            Result.failure(e)
        }
    }

    suspend fun editPostDescription(postId: String, newDescription: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val firebaseUser = auth.currentUser ?: return@withContext Result.failure(Exception("Usuário não logado."))
            val postRef = db.collection("posts").document(postId)

            val snapshot = postRef.get().await()
            val post = snapshot.toObject(Post::class.java) ?: return@withContext Result.failure(Exception("Post não encontrado."))

            if (post.authorUid != firebaseUser.uid) {
                return@withContext Result.failure(Exception("Apenas o autor pode editar o post."))
            }

            postRef.update("description", newDescription).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PostRepository", "editPostDescription failed", e)
            Result.failure(e)
        }
    }
    suspend fun getPostById(postId: String): Post? {
        return try {
            val doc = db.collection("posts").document(postId).get().await()
            documentToPostSafe(doc)
        } catch (e: Exception) {
            Log.e("PostRepository", "getPostById failed", e)
            null
        }
    }

}
