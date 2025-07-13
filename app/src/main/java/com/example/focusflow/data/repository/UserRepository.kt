package com.example.focusflow.data.repository

import android.util.Log
import com.example.focusflow.data.model.UserDto
import com.example.focusflow.domain.model.User
import com.example.focusflow.domain.repository.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val auth: FirebaseAuth,
    ) : IUserRepository {
        private val currentUserId: String?
            get() = auth.currentUser?.uid

        override suspend fun createUserProfile(user: User): Result<Boolean> =
            withContext(Dispatchers.IO) {
                try {
                    val userId =
                        currentUserId ?: return@withContext Result.failure<Boolean>(
                            IllegalStateException("Usuário não está autenticado"),
                        )

                    val userDto = UserDto.fromDomain(user.copy(id = userId))

                    val userRef = firestore.collection("users").document(userId)
                    userRef.set(userDto).await()

                    Result.success(true)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

        override suspend fun getUserProfile(): Result<User?> =
            withContext(Dispatchers.IO) {
                try {
                    val userId =
                        currentUserId ?: return@withContext Result.failure<User?>(
                            IllegalStateException("Usuário não está autenticado"),
                        )

                    val userRef = firestore.collection("users").document(userId)
                    val userSnapshot = userRef.get().await()

                    val userDto = userSnapshot.toObject<UserDto>()
                    Result.success(userDto?.toDomain())
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

        override suspend fun getCurrentUser(): User? =
            withContext(Dispatchers.IO) {
                try {
                    val currentUser = auth.currentUser ?: return@withContext null

                    val userDoc =
                        firestore.collection("users")
                            .document(currentUser.uid)
                            .get()
                            .await()

                    if (userDoc.exists()) {
                        val userData = userDoc.data ?: mapOf()
                        return@withContext User(
                            id = currentUser.uid,
                            name = userData["name"] as? String ?: currentUser.displayName ?: "User",
                            email = currentUser.email ?: "",
                            level = (userData["level"] as? Long)?.toInt() ?: 1,
                            points = (userData["points"] as? Long)?.toInt() ?: 0,
                            createdAt =
                                userData["createdAt"] as? Long
                                    ?: currentUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                        )
                    } else {
                        return@withContext User(
                            id = currentUser.uid,
                            name = currentUser.displayName ?: "User",
                            email = currentUser.email ?: "",
                            level = 1,
                            points = 0,
                            createdAt =
                                currentUser.metadata?.creationTimestamp
                                    ?: System.currentTimeMillis(),
                        )
                    }
                } catch (e: Exception) {
                    Log.e("UserRepository", "Error getting current user: ${e.message}")
                    return@withContext null
                }
            }

        override fun getUserProfileFlow(): Flow<User?> {
            val userId = currentUserId ?: return flowOf(null)

            return callbackFlow {
                val userRef = firestore.collection("users").document(userId)

                val subscription =
                    userRef.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(null)
                            return@addSnapshotListener
                        }

                        val userDto = snapshot?.toObject<UserDto>()
                        trySend(userDto?.toDomain())
                    }

                awaitClose { subscription.remove() }
            }
        }

        override suspend fun updateUserPoints(pointsToAdd: Int): Result<Pair<Int, Int>> =
            withContext(Dispatchers.IO) {
                try {
                    val userId =
                        currentUserId ?: return@withContext Result.failure<Pair<Int, Int>>(
                            IllegalStateException("Usuário não está autenticado"),
                        )

                    val userRef = firestore.collection("users").document(userId)

                    val result =
                        firestore.runTransaction { transaction ->
                            val userSnapshot = transaction.get(userRef)
                            val userDto = userSnapshot.toObject<UserDto>() ?: UserDto()

                            val newPoints = userDto.points + pointsToAdd
                            val pointsPerLevel = 100
                            val newLevel = (newPoints / pointsPerLevel) + 1

                            transaction.update(userRef, "points", newPoints)
                            transaction.update(userRef, "level", newLevel)

                            Pair(newPoints, newLevel)
                        }.await()

                    Result.success(result)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }
