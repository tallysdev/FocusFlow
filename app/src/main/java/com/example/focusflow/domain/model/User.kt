package com.example.focusflow.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val level: Int = 1,
    val points: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
)
