package com.example.focusflow.ui.screens.trophies

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusflow.R
import com.example.focusflow.data.repository.TrophiesRepository
import com.example.focusflow.domain.model.Activity
import com.example.focusflow.domain.repository.IActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class Trophy(
    val id: Int,
    val name: String,
    val description: String,
    val dateAchieved: String?, // null se ainda não conquistado
    val imageUrl: Int,
)

data class TrophyAchievedEvent(val trophy: Trophy)

@HiltViewModel
class TrophiesViewModel
    @Inject
    constructor(
        private val activityRepository: IActivityRepository,
        private val trophiesRepository: TrophiesRepository,
    ) : ViewModel() {
        private val _trophies = mutableStateListOf<Trophy>()
        val trophies: List<Trophy> = _trophies

        private val _trophyAchievedEvent = MutableSharedFlow<TrophyAchievedEvent>()
        val trophyAchievedEvent = _trophyAchievedEvent.asSharedFlow()

        init {
            loadTrophies()
            checkForNewAchievements()
        }

        private fun loadTrophies() {
            viewModelScope.launch {
                val savedTrophies = trophiesRepository.getTrophies()
                if (savedTrophies.isEmpty()) {
                    _trophies.addAll(
                        listOf(
                            Trophy(
                                1,
                                "First Task Completed",
                                "Complete your first task.",
                                null,
                                R.drawable.trophy,
                            ),
                            Trophy(
                                2,
                                "Weekly Streak",
                                "Complete tasks for 7 consecutive days.",
                                null,
                                R.drawable.trophy,
                            ),
                            Trophy(
                                3,
                                "10 Tasks Completed",
                                "Complete 10 tasks.",
                                null,
                                R.drawable.trophy,
                            ),
                            Trophy(
                                4,
                                "Category Master",
                                "Complete a task in every category.",
                                null,
                                R.drawable.trophy,
                            ),
                            Trophy(
                                5,
                                "Perfect Day",
                                "Complete all your tasks in one day.",
                                null,
                                R.drawable.trophy,
                            ),
                            Trophy(
                                6,
                                "Work Expert",
                                "Complete 5 work-related tasks.",
                                null,
                                R.drawable.trophy,
                            ),
                            Trophy(
                                7,
                                "Personal Growth",
                                "Complete 5 personal tasks.",
                                null,
                                R.drawable.trophy,
                            ),
                            Trophy(
                                8,
                                "Event Organizer",
                                "Complete 5 event tasks.",
                                null,
                                R.drawable.trophy,
                            ),
                            Trophy(9, "Traveler", "Complete 5 travel tasks.", null, R.drawable.trophy),
                        ),
                    )
                    trophiesRepository.saveTrophies(_trophies)
                } else {
                    _trophies.clear()
                    _trophies.addAll(savedTrophies)
                }
            }
        }

        fun checkForNewAchievements() {
            viewModelScope.launch {
                val activities = activityRepository.getActivitiesFlow().first()
                val completedActivities = activities.filter { it.status == "completed" }

                checkFirstTaskCompleted(completedActivities)
                checkTasksCompletedCount(completedActivities)
                checkCategoryMaster(completedActivities)
                checkWeeklyStreak(completedActivities)
                checkCategorySpecificTrophies(completedActivities)
                checkPerfectDay(activities, completedActivities)

                trophiesRepository.saveTrophies(_trophies)
            }
        }

        private fun checkFirstTaskCompleted(completedActivities: List<Activity>) {
            val firstTaskTrophy = _trophies.find { it.id == 1 }
            if (firstTaskTrophy != null && firstTaskTrophy.dateAchieved == null && completedActivities.isNotEmpty()) {
                updateTrophy(firstTaskTrophy.id)
            }
        }

        private fun checkTasksCompletedCount(completedActivities: List<Activity>) {
            val tenTasksTrophy = _trophies.find { it.id == 3 }
            if (tenTasksTrophy != null && tenTasksTrophy.dateAchieved == null && completedActivities.size >= 10) {
                updateTrophy(tenTasksTrophy.id)
            }
        }

        private fun checkCategoryMaster(completedActivities: List<Activity>) {
            val categoryMasterTrophy = _trophies.find { it.id == 4 }
            if (categoryMasterTrophy != null && categoryMasterTrophy.dateAchieved == null) {
                val categories = setOf("Work", "Travel", "Event", "Personal")
                val completedCategories = completedActivities.map { it.category }.toSet()

                if (categories.all { completedCategories.contains(it) }) {
                    updateTrophy(categoryMasterTrophy.id)
                }
            }
        }

        private fun checkWeeklyStreak(completedActivities: List<Activity>) {
            val weeklyStreakTrophy = _trophies.find { it.id == 2 }
            if (weeklyStreakTrophy != null && weeklyStreakTrophy.dateAchieved == null) {
                // Agrupar atividades concluídas por dia
                val completionDates =
                    completedActivities.mapNotNull { activity ->
                        activity.completionDate?.let { Date(it) }
                    }.sortedDescending()

                if (completionDates.size >= 7) {
                    // Verificar se há 7 dias consecutivos
                    val consecutiveDays = hasConsecutiveDays(completionDates, 7)
                    if (consecutiveDays) {
                        updateTrophy(weeklyStreakTrophy.id)
                    }
                }
            }
        }

        private fun checkCategorySpecificTrophies(completedActivities: List<Activity>) {
            // Work Expert (ID: 6)
            val workExpertTrophy = _trophies.find { it.id == 6 }
            if (workExpertTrophy != null && workExpertTrophy.dateAchieved == null) {
                val workTasksCompleted = completedActivities.count { it.category == "Work" }
                if (workTasksCompleted >= 5) {
                    updateTrophy(workExpertTrophy.id)
                }
            }

            // Personal Growth (ID: 7)
            val personalGrowthTrophy = _trophies.find { it.id == 7 }
            if (personalGrowthTrophy != null && personalGrowthTrophy.dateAchieved == null) {
                val personalTasksCompleted = completedActivities.count { it.category == "Personal" }
                if (personalTasksCompleted >= 5) {
                    updateTrophy(personalGrowthTrophy.id)
                }
            }

            // Event Organizer (ID: 8)
            val eventOrganizerTrophy = _trophies.find { it.id == 8 }
            if (eventOrganizerTrophy != null && eventOrganizerTrophy.dateAchieved == null) {
                val eventTasksCompleted = completedActivities.count { it.category == "Event" }
                if (eventTasksCompleted >= 5) {
                    updateTrophy(eventOrganizerTrophy.id)
                }
            }

            // Traveler (ID: 9)
            val travelerTrophy = _trophies.find { it.id == 9 }
            if (travelerTrophy != null && travelerTrophy.dateAchieved == null) {
                val travelTasksCompleted = completedActivities.count { it.category == "Travel" }
                if (travelTasksCompleted >= 5) {
                    updateTrophy(travelerTrophy.id)
                }
            }
        }

        private fun checkPerfectDay(
            allActivities: List<Activity>,
            completedActivities: List<Activity>,
        ) {
            val perfectDayTrophy = _trophies.find { it.id == 5 }
            if (perfectDayTrophy != null && perfectDayTrophy.dateAchieved == null) {
                // Agrupar todas as atividades por dia de vencimento
                val activitiesByDueDate = allActivities.groupBy { it.dueDate }

                // Verificar se em algum dia todas as atividades foram concluídas
                for ((dueDate, activities) in activitiesByDueDate) {
                    if (dueDate != null && activities.size > 1) {
                        val allCompletedForDay =
                            activities.all {
                                completedActivities.any { completed -> completed.id == it.id }
                            }

                        if (allCompletedForDay) {
                            updateTrophy(perfectDayTrophy.id)
                            break
                        }
                    }
                }
            }
        }

        private fun hasConsecutiveDays(
            dates: List<Date>,
            requiredDays: Int,
        ): Boolean {
            if (dates.size < requiredDays) return false

            // Converter para dias únicos (ignorando horas, minutos, segundos)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val uniqueDays = dates.map { dateFormat.format(it) }.toSet().toList().sorted()

            if (uniqueDays.size < requiredDays) return false

            // Verificar sequência de dias consecutivos
            var consecutiveDaysCount = 1
            var maxConsecutiveDays = 1

            for (i in 1 until uniqueDays.size) {
                val previousDate =
                    Calendar.getInstance().apply {
                        time = dateFormat.parse(uniqueDays[i - 1])!!
                        add(Calendar.DAY_OF_MONTH, -1)
                    }.time
                val currentDate = dateFormat.parse(uniqueDays[i])!!

                if (dateFormat.format(previousDate) == dateFormat.format(currentDate)) {
                    consecutiveDaysCount++
                    maxConsecutiveDays = maxOf(maxConsecutiveDays, consecutiveDaysCount)
                } else {
                    consecutiveDaysCount = 1
                }
            }

            return maxConsecutiveDays >= requiredDays
        }

        private fun updateTrophy(trophyId: Int) {
            val index = _trophies.indexOfFirst { it.id == trophyId }
            if (index != -1) {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val updatedTrophy = _trophies[index].copy(dateAchieved = currentDate)
                _trophies[index] = updatedTrophy

                // Emitir evento de troféu conquistado
                viewModelScope.launch {
                    _trophyAchievedEvent.emit(TrophyAchievedEvent(updatedTrophy))
                }
            }
        }
    }
