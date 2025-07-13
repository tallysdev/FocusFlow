package com.example.focusflow.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusflow.data.repository.TrophiesRepository
import com.example.focusflow.domain.model.User
import com.example.focusflow.domain.repository.IActivityRepository
import com.example.focusflow.domain.repository.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val userRepository: IUserRepository,
        private val activityRepository: IActivityRepository,
        private val trophiesRepository: TrophiesRepository,
        private val auth: FirebaseAuth,
    ) : ViewModel() {
        private val _userState = MutableStateFlow<ProfileState>(ProfileState.Loading)
        val userState: StateFlow<ProfileState> = _userState.asStateFlow()

        private val _stats = MutableStateFlow(UserStats(0, 0, 0))
        val stats: StateFlow<UserStats> = _stats.asStateFlow()

        private val _activities = MutableStateFlow<List<UserActivity>>(emptyList())
        val activities: StateFlow<List<UserActivity>> = _activities.asStateFlow()

        init {
            loadUserData()
            loadUserStats()
            loadRecentActivities()
        }

        private fun loadUserData() {
            viewModelScope.launch {
                try {
                    val currentUser = userRepository.getCurrentUser()
                    currentUser?.let {
                        _userState.value = ProfileState.Success(it)
                        Log.d("ProfileViewModel", "User data loaded: $it")
                    } ?: run {
                        _userState.value = ProfileState.Error("User not found")
                    }
                } catch (e: Exception) {
                    _userState.value = ProfileState.Error(e.message ?: "Unknown error")
                }
            }
        }

        private fun loadUserStats() {
            viewModelScope.launch {
                try {
                    // Get completed activities count
                    val activities = activityRepository.getActivitiesFlow().first()
                    val completedActivitiesCount = activities.count { it.status == "completed" }

                    // Get streak count (simplified version)
                    val streakCount = calculateStreak(activities)

                    // Get trophies count
                    val trophies = trophiesRepository.getTrophies()
                    val trophiesCount = trophies.count { it.dateAchieved != null }

                    _stats.value =
                        UserStats(
                            tasksCompleted = completedActivitiesCount,
                            streaks = streakCount,
                            trophies = trophiesCount,
                        )
                } catch (e: Exception) {
                    // Handle error but keep the UI showing at least zeros
                    _stats.value = UserStats(0, 0, 0)
                }
            }
        }

        private fun loadRecentActivities() {
            viewModelScope.launch {
                try {
                    val activities = mutableListOf<UserActivity>()

                    // Add recent completed activities
                    val completedActivities =
                        activityRepository.getActivitiesFlow().first()
                            .filter { it.status == "completed" && it.completionDate != null }
                            .sortedByDescending { it.completionDate }
                            .take(3)

                    completedActivities.forEach { activity ->
                        activities.add(
                            UserActivity(
                                text = "Completed '${activity.title}'",
                                time = formatTimeAgo(activity.completionDate ?: 0),
                            ),
                        )
                    }

                    // Add trophy achievements
                    val trophies =
                        trophiesRepository.getTrophies()
                            .filter { it.dateAchieved != null }
                            .sortedByDescending { it.dateAchieved }
                            .take(2)

                    trophies.forEach { trophy ->
                        activities.add(
                            UserActivity(
                                text = "Earned trophy: ${trophy.name}",
                                time = trophy.dateAchieved ?: "Recently",
                            ),
                        )
                    }

                    // Add level up events (fictício para demonstração)
                    if (userState.value is ProfileState.Success) {
                        val user = (userState.value as ProfileState.Success).user
                        if (user.level > 1) {
                            activities.add(
                                UserActivity(
                                    text = "Reached Level ${user.level}",
                                    time = "Recently",
                                ),
                            )
                        }
                    }

                    _activities.value = activities.take(3) // Limit to 3 activities
                } catch (e: Exception) {
                    // Keep empty list on error
                }
            }
        }

        private fun calculateStreak(activities: List<com.example.focusflow.domain.model.Activity>): Int {
            // Simplified streak calculation
            return activities
                .filter { it.status == "completed" }
                .mapNotNull { it.completionDate?.let { date -> date / (24 * 60 * 60 * 1000) } }
                .distinct()
                .size.coerceAtMost(80) // Placeholder value, limited to 80
        }

        private fun formatTimeAgo(timestamp: Long): String {
            val currentTime = System.currentTimeMillis()
            val diffTime = currentTime - timestamp

            return when {
                diffTime < 60 * 60 * 1000 -> "Just now"
                diffTime < 24 * 60 * 60 * 1000 -> "${diffTime / (60 * 60 * 1000)} hours ago"
                diffTime < 7 * 24 * 60 * 60 * 1000 -> "${diffTime / (24 * 60 * 60 * 1000)} days ago"
                diffTime < 30 * 24 * 60 * 60 * 1000 -> "${diffTime / (7 * 24 * 60 * 60 * 1000)} weeks ago"
                else -> "${diffTime / (30 * 24 * 60 * 60 * 1000)} months ago"
            }
        }

        fun logout() {
            viewModelScope.launch {
                try {
                    auth.signOut()
                } catch (e: Exception) {
                    // Log de erro ou tratamento adicional, se necessário
                }
            }
        }
    }

sealed class ProfileState {
    object Loading : ProfileState()

    data class Success(val user: User) : ProfileState()

    data class Error(val message: String) : ProfileState()
}

data class UserStats(
    val tasksCompleted: Int,
    val streaks: Int,
    val trophies: Int,
)

data class UserActivity(
    val text: String,
    val time: String,
)
