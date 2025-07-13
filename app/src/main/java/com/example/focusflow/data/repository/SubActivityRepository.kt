package com.example.focusflow.data.repository

import android.util.Log
import com.example.focusflow.data.model.SubActivityDto
import com.example.focusflow.domain.model.SubActivity
import com.example.focusflow.domain.repository.ISubActivityRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubActivityRepository
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val auth: FirebaseAuth,
    ) : ISubActivityRepository {
        private val currentUserId: String?
            get() = auth.currentUser?.uid

        override suspend fun addSubActivity(
            activityId: String,
            subActivity: SubActivity,
        ): Result<String> =
            withContext(Dispatchers.IO) {
                try {
                    val userId =
                        currentUserId ?: return@withContext Result.failure<String>(
                            IllegalStateException("Usuário não está autenticado"),
                        )

                    Log.d("SubActivityRepository", "Adding subactivity: $subActivity to activity: $activityId")

                    val subActivitiesRef =
                        firestore.collection("users")
                            .document(userId)
                            .collection("activities")
                            .document(activityId)
                            .collection("subActivities")

                    val subActivityDto = SubActivityDto.fromDomain(subActivity.copy(activityId = activityId))

                    val docRef = subActivitiesRef.add(subActivityDto).await()
                    Result.success(docRef.id)
                } catch (e: Exception) {
                    Log.e("SubActivityRepository", "Error adding subactivity: ${e.message}", e)
                    Result.failure(e)
                }
            }

        override fun getSubActivitiesFlow(activityId: String): Flow<List<SubActivity>> {
            val userId = currentUserId ?: return flowOf(emptyList())

            return callbackFlow {
                val subActivitiesRef =
                    firestore.collection("users")
                        .document(userId)
                        .collection("activities")
                        .document(activityId)
                        .collection("subActivities")

                val subscription =
                    subActivitiesRef.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(emptyList())
                            return@addSnapshotListener
                        }

                        val subActivities =
                            snapshot?.documents?.mapNotNull { doc ->
                                val subActivityDto = doc.toObject(SubActivityDto::class.java)
                                subActivityDto?.copy(id = doc.id)?.toDomain()
                            } ?: emptyList()

                        trySend(subActivities)
                    }

                awaitClose { subscription.remove() }
            }
        }

        override suspend fun updateSubActivityStatus(
            activityId: String,
            subActivityId: String,
            newStatus: String,
        ): Result<Boolean> =
            withContext(Dispatchers.IO) {
                try {
                    val userId =
                        currentUserId ?: return@withContext Result.failure<Boolean>(
                            IllegalStateException("Usuário não está autenticado"),
                        )

                    val subActivityRef =
                        firestore.collection("users")
                            .document(userId)
                            .collection("activities")
                            .document(activityId)
                            .collection("subActivities")
                            .document(subActivityId)

                    subActivityRef.update("status", newStatus).await()
                    Result.success(true)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }
