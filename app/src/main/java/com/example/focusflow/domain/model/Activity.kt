package com.example.focusflow.domain.model

data class Activity(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val startDate: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val status: String = "pending",
    val category: String = "",
    val difficultyLevel: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val subActivities: List<SubActivity> = emptyList(),
    val completionDate: Long? = null,
)
