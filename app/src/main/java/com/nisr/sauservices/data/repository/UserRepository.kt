package com.nisr.sauservices.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.nisr.sauservices.data.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun signIn(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithCredential(credential: AuthCredential): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, userData: Map<String, Any>): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                firestore.collection("users").document(user.uid).set(userData).await()
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun registerUser(user: User) {
        firestore.collection("users").document(user.id).set(user)
    }

    suspend fun getUserData(uid: String): Result<Map<String, Any>?> {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            Result.success(document.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUserData(uid: String, userData: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("users").document(uid).set(userData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
