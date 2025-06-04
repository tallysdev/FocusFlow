package com.example.focusflow.data.repository

import com.example.focusflow.ui.screens.login.LoginResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): LoginResult {
        return try {
            // Firebase Login logic
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            LoginResult.Success
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "An unknown error occurred")
        }
    }
}