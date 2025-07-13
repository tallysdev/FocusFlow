package com.example.focusflow.domain.repository

import com.example.focusflow.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun createUserProfile(user: User): Result<Boolean>

    suspend fun getUserProfile(): Result<User?>

    suspend fun getCurrentUser(): User?

    fun getUserProfileFlow(): Flow<User?>

    suspend fun updateUserPoints(pointsToAdd: Int): Result<Pair<Int, Int>>
}
