package com.example.focusflow.domain.repository

interface ISubtaskGeneratorRepository {
    suspend fun generateSubtasks(
        title: String,
        category: String,
        description: String,
    ): Result<List<String>>
}
