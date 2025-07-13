package com.example.focusflow.domain.repository

import com.example.focusflow.domain.model.SubActivity
import kotlinx.coroutines.flow.Flow

interface ISubActivityRepository {
    suspend fun addSubActivity(
        activityId: String,
        subActivity: SubActivity,
    ): Result<String>

    fun getSubActivitiesFlow(activityId: String): Flow<List<SubActivity>>

    suspend fun updateSubActivityStatus(
        activityId: String,
        subActivityId: String,
        newStatus: String,
    ): Result<Boolean>
}
