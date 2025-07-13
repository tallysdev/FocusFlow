package com.example.focusflow.data.repository

import android.util.Log
import com.example.focusflow.data.model.ActivityDto
import com.example.focusflow.data.model.SubActivityDto
import com.example.focusflow.domain.model.Activity
import com.example.focusflow.domain.repository.IActivityRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ActivityRepository
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val auth: FirebaseAuth,
    ) : IActivityRepository {
        private val currentUserId: String?
            get() = auth.currentUser?.uid

        override suspend fun addActivity(activity: Activity): Result<String> =
            withContext(Dispatchers.IO) {
                try {
                    val userId =
                        currentUserId ?: return@withContext Result.failure<String>(
                            IllegalStateException("Usuário não está autenticado"),
                        )

                    val activityWithoutSubActivities = activity.copy(subActivities = emptyList())
                    val activityDto = ActivityDto.fromDomain(activityWithoutSubActivities)

                    val activityRef =
                        firestore.collection("users")
                            .document(userId)
                            .collection("activities")
                            .add(activityDto).await()

                    val activityId = activityRef.id

                    Log.d("ActivityRepository", "Activity saved with ID: $activityId")

                    Log.d(
                        "ActivityRepository",
                        "Saving activity with ${activity.subActivities.size} subactivities",
                    )

                    activity.subActivities.forEachIndexed { index, subActivity ->
                        Log.d(
                            "ActivityRepository",
                            "Saving subactivity ${index + 1}: ${subActivity.name}",
                        )

                        val subActivityWithActivityId = subActivity.copy(activityId = activityId)
                        val subActivityDto = SubActivityDto.fromDomain(subActivityWithActivityId)

                        firestore.collection("users")
                            .document(userId)
                            .collection("activities")
                            .document(activityId)
                            .collection("subActivities")
                            .add(subActivityDto).await()
                    }

                    Result.success(activityId)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

        override suspend fun getActivities(): Result<List<Activity>> =
            withContext(Dispatchers.IO) {
                try {
                    val userId =
                        currentUserId ?: return@withContext Result.failure<List<Activity>>(
                            IllegalStateException("Usuário não está autenticado"),
                        )

                    val activitiesRef =
                        firestore.collection("users")
                            .document(userId)
                            .collection("activities")

                    val querySnapshot = activitiesRef.get().await()

                    val activities =
                        querySnapshot.documents.mapNotNull { doc ->
                            val activityDto = doc.toObject(ActivityDto::class.java)
                            activityDto?.copy(id = doc.id)?.toDomain()
                        }

                    Result.success(activities)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

        override suspend fun deleteActivity(activityId: String): Result<Unit> =
            withContext(Dispatchers.IO) {
                try {
                    val userId =
                        auth.currentUser?.uid ?: return@withContext Result.failure(
                            IllegalStateException("User not authenticated"),
                        )

                    // 1. Primeiro, obtenha todas as subtarefas fora da transação
                    val subActivitiesRef =
                        firestore.collection("users")
                            .document(userId)
                            .collection("activities")
                            .document(activityId)
                            .collection("subActivities")

                    val subActivitiesSnapshot = subActivitiesRef.get().await()
                    val subActivityIds = subActivitiesSnapshot.documents.map { it.id }

                    // 2. Agora, exclua cada subtarefa
                    for (subActivityId in subActivityIds) {
                        val subActivityRef =
                            firestore.collection("users")
                                .document(userId)
                                .collection("activities")
                                .document(activityId)
                                .collection("subActivities")
                                .document(subActivityId)

                        subActivityRef.delete().await()
                    }

                    // 3. Finalmente, exclua a atividade principal
                    val activityRef =
                        firestore.collection("users")
                            .document(userId)
                            .collection("activities")
                            .document(activityId)

                    activityRef.delete().await()

                    Log.d(
                        "ActivityRepository",
                        "Activity $activityId and ${subActivityIds.size} subtasks deleted successfully",
                    )
                    return@withContext Result.success(Unit)
                } catch (e: Exception) {
                    Log.e("ActivityRepository", "Error deleting activity: ${e.message}", e)
                    return@withContext Result.failure(e)
                }
            }

        override fun getActivitiesFlow(): Flow<List<Activity>> {
            val userId = currentUserId ?: return flowOf(emptyList())

            return callbackFlow {
                val activitiesRef =
                    firestore.collection("users")
                        .document(userId)
                        .collection("activities")

                val subscription =
                    activitiesRef.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(emptyList())
                            return@addSnapshotListener
                        }

                        val activitiesWithSubactivities = mutableListOf<Activity>()
                        val activities =
                            snapshot?.documents?.mapNotNull { doc ->
                                val activityDto = doc.toObject(ActivityDto::class.java)
                                activityDto?.copy(id = doc.id)?.toDomain()
                            } ?: emptyList()

                        CoroutineScope(Dispatchers.IO).launch {
                            activities.forEach { activity ->
                                try {
                                    val subActivitiesSnapshot =
                                        firestore.collection("users")
                                            .document(userId)
                                            .collection("activities")
                                            .document(activity.id)
                                            .collection("subActivities")
                                            .get()
                                            .await()

                                    val subActivities =
                                        subActivitiesSnapshot.documents.mapNotNull { subDoc ->
                                            val subActivityDto =
                                                subDoc.toObject(SubActivityDto::class.java)
                                            subActivityDto?.copy(id = subDoc.id)?.toDomain()
                                        }

                                    activitiesWithSubactivities.add(activity.copy(subActivities = subActivities))
                                } catch (e: Exception) {
                                    Log.e(
                                        "ActivityRepository",
                                        "Error fetching subactivities: ${e.message}",
                                    )
                                    activitiesWithSubactivities.add(activity)
                                }
                            }
                            trySend(activitiesWithSubactivities)
                        }
                    }

                awaitClose { subscription.remove() }
            }
        }

        override suspend fun updateActivityStatus(
            activityId: String,
            status: String,
            additionalUpdates: Map<String, Any?>,
        ): Result<Unit> =
            withContext(Dispatchers.IO) {
                try {
                    val userId =
                        auth.currentUser?.uid ?: return@withContext Result.failure(
                            IllegalStateException("User not authenticated"),
                        )

                    val activityRef =
                        firestore.collection("users")
                            .document(userId)
                            .collection("activities")
                            .document(activityId)

                    val updates =
                        if (status == "completed") {
                            mapOf(
                                "status" to status,
                                "completionDate" to System.currentTimeMillis(),
                                "updatedAt" to Timestamp.now(),
                            )
                        } else {
                            mapOf(
                                "status" to status,
                                "completionDate" to null,
                                "updatedAt" to Timestamp.now(),
                            )
                        }

                    // Combinar os updates base com os adicionais
                    val allUpdates = updates + additionalUpdates

                    activityRef.update(allUpdates).await()

                    return@withContext Result.success(Unit)
                } catch (e: Exception) {
                    return@withContext Result.failure(e)
                }
            }
    }
