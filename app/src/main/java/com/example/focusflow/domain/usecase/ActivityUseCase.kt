package com.example.focusflow.domain.usecase

import com.example.focusflow.domain.model.Activity
import com.example.focusflow.domain.repository.IActivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActivitiesUseCase
    @Inject
    constructor(
        private val activityRepository: IActivityRepository,
    ) {
        operator fun invoke(): Flow<List<Activity>> {
            return activityRepository.getActivitiesFlow()
        }
    }

class AddActivityUseCase
    @Inject
    constructor(
        private val activityRepository: IActivityRepository,
    ) {
        suspend operator fun invoke(activity: Activity): Result<String> {
            return activityRepository.addActivity(activity)
        }
    }
