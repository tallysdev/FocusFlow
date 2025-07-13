package com.example.focusflow.domain.model

data class SubActivity(
    val id: String = "",
    val activityId: String = "",
    val name: String = "",
    val status: String = "pending",
    val idealDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val priority: Int = 0,
)
