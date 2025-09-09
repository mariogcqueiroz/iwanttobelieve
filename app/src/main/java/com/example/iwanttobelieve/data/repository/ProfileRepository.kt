package com.example.iwanttobelieve.data.repository

import android.util.Log
import com.example.iwanttobelieve.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun getUserProfile(): User? {
        val currentUser = auth.currentUser ?: return null
        val snapshot = db.collection("users").document(currentUser.uid).get().await()
        return snapshot.toObject(User::class.java)
    }

    suspend fun updateUserProfile(newName: String, newEmail: String?): Result<Unit> {
        try {
            val currentUser = auth.currentUser
                ?: return Result.failure(Exception("Usuário não logado."))

            // Atualizar email no FirebaseAuth se for diferente e não nulo
            if (!newEmail.isNullOrBlank() && newEmail != currentUser.email) {
                try {
                    currentUser.updateEmail(newEmail).await()
                } catch (e: Exception) {
                    Log.e("ProfileRepository", "Falha ao atualizar email no Auth", e)
                    return Result.failure(e)
                }
            }

            // Atualizar displayName no Auth
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            currentUser.updateProfile(profileUpdates).await()

            // Atualizar no Firestore
            val updates = hashMapOf<String, Any>(
                "name" to newName
            )
            if (!newEmail.isNullOrBlank()) {
                updates["email"] = newEmail
            }

            db.collection("users").document(currentUser.uid).update(updates).await()
            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProfileRepository", "updateUserProfile failed", e)
            return Result.failure(e)
        }
    }
}
