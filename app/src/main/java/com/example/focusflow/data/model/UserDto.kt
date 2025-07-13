package com.example.focusflow.data.model

import com.example.focusflow.domain.model.User
import com.google.firebase.Timestamp
import java.util.Date

data class UserDto(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val level: Int = 1,
    val points: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
) {
    fun toDomain(): User {
        return User(
            id = id,
            name = name,
            email = email,
            level = level,
            points = points,
            createdAt = createdAt.toDate().time,
        )
    }

    companion object {
        fun fromDomain(user: User): UserDto {
            return UserDto(
                id = user.id,
                name = user.name,
                email = user.email,
                level = user.level,
                points = user.points,
                createdAt = Timestamp(Date(user.createdAt)),
            )
        }
    }
}
