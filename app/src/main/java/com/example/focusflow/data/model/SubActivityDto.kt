package com.example.focusflow.data.model

import com.example.focusflow.domain.model.SubActivity
import com.google.firebase.Timestamp
import java.util.Date

data class SubActivityDto(
    val id: String = "",
    val activityId: String = "",
    val name: String = "",
    val status: String = "pending",
    val idealDate: Timestamp? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val priority: Int = 0,
) {
    fun toDomain(): SubActivity {
        return SubActivity(
            id = id,
            activityId = activityId,
            name = name,
            status = status,
            idealDate = idealDate?.toDate()?.time,
            createdAt = createdAt.toDate().time,
            priority = priority,
        )
    }

    companion object {
        fun fromDomain(subActivity: SubActivity): SubActivityDto {
            return SubActivityDto(
                id = subActivity.id,
                activityId = subActivity.activityId,
                name = subActivity.name,
                status = subActivity.status,
                idealDate = subActivity.idealDate?.let { Timestamp(Date(it)) },
                createdAt = Timestamp(Date(subActivity.createdAt)),
                priority = subActivity.priority,
            )
        }
    }
}
