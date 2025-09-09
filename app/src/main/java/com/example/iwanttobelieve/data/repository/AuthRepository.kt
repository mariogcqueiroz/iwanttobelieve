package com.example.iwanttobelieve.data.repository

import android.util.Log
import com.example.iwanttobelieve.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUser() = auth.currentUser

    suspend fun signIn(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return@withContext Result.failure(Exception("Falha ao logar usuário."))

            // Se o documento do usuário não existir no Firestore, cria um perfil básico
            val snapshot = db.collection("users").document(firebaseUser.uid).get().await()
            if (!snapshot.exists()) {
                val user = User(uid = firebaseUser.uid, name = firebaseUser.displayName ?: "Sem nome", email = email)
                db.collection("users").document(firebaseUser.uid).set(user).await()
                Log.d("AuthRepository", "Perfil criado automaticamente no Firestore após signIn.")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "signIn failed", e)
            Result.failure(e)
        }
    }

    suspend fun signUp(name: String, email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return@withContext Result.failure(Exception("Falha ao criar usuário."))

            // Salva o perfil no Firestore
            val user = User(uid = firebaseUser.uid, name = name, email = email)
            db.collection("users").document(firebaseUser.uid).set(user).await()

            // Opcional: atualizar displayName do FirebaseAuth
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "signUp failed", e)
            Result.failure(e)
        }
    }

    fun signOut() = auth.signOut()
}
