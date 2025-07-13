package com.example.focusflow.data.model

import com.example.focusflow.domain.model.Activity
import com.google.firebase.Timestamp
import java.util.Date

data class ActivityDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val startDate: Timestamp = Timestamp.now(),
    val dueDate: Timestamp? = null,
    val status: String = "pending",
    val category: String = "",
    val difficultyLevel: Int = 1,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val completionDate: Long? = null, // Mudado de Timestamp? para Long?
) {
    fun toDomain(): Activity {
        return Activity(
            id = id,
            title = title,
            description = description,
            startDate = startDate.toDate().time,
            dueDate = dueDate?.toDate()?.time,
            status = status,
            category = category,
            difficultyLevel = difficultyLevel,
            createdAt = createdAt.toDate().time,
            updatedAt = updatedAt.toDate().time,
            completionDate = completionDate,
            subActivities = emptyList(),
        )
    }

    companion object {
        fun fromDomain(activity: Activity): ActivityDto {
            return ActivityDto(
                id = activity.id,
                title = activity.title,
                description = activity.description,
                startDate = Timestamp(Date(activity.startDate)),
                dueDate = activity.dueDate?.let { Timestamp(Date(it)) },
                status = activity.status,
                category = activity.category,
                difficultyLevel = activity.difficultyLevel,
                createdAt = Timestamp(Date(activity.createdAt)),
                updatedAt = Timestamp(Date(activity.updatedAt)),
                completionDate = activity.completionDate,
            )
        }
    }
}
