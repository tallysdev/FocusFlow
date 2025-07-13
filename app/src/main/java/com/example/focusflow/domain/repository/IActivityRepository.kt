package com.example.focusflow.domain.repository

import com.example.focusflow.domain.model.Activity
import kotlinx.coroutines.flow.Flow

interface IActivityRepository {
    suspend fun addActivity(activity: Activity): Result<String>

    suspend fun getActivities(): Result<List<Activity>>

    suspend fun deleteActivity(activityId: String): Result<Unit>

    fun getActivitiesFlow(): Flow<List<Activity>>

    suspend fun updateActivityStatus(
        activityId: String,
        status: String,
        additionalFields: Map<String, Any?> = emptyMap(),
    ): Result<Unit>
}
