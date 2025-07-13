package com.example.focusflow.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusflow.data.repository.TrophiesRepository
import com.example.focusflow.domain.model.Activity
import com.example.focusflow.domain.repository.IActivityRepository
import com.example.focusflow.domain.repository.ISubActivityRepository
import com.example.focusflow.ui.screens.trophies.Trophy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val activityRepository: IActivityRepository,
        private val subActivityRepository: ISubActivityRepository,
        private val trophiesRepository: TrophiesRepository,
    ) : ViewModel() {
        // Estado para a lista de atividades
        private val _activitiesState = MutableStateFlow<UiState<List<Activity>>>(UiState.Loading)
        val activitiesState: StateFlow<UiState<List<Activity>>> = _activitiesState.asStateFlow()

        // Estado para o filtro selecionado
        private val _selectedFilter = MutableStateFlow("Pending")
        val selectedFilter = _selectedFilter.asStateFlow()

        // Estados para UI
        var searchQuery by mutableStateOf("")
            private set

        init {
            loadActivities()

            viewModelScope.launch {
                delay(1000)
                when (val state = _activitiesState.value) {
                    is UiState.Success -> {
                        state.data.forEach { activity ->
                            checkAndUpdateActivityStatus(activity.id)
                        }
                    }

                    else -> {}
                }
            }
        }

        private fun loadActivities() {
            viewModelScope.launch {
                try {
                    // Observar atividades em tempo real
                    activityRepository.getActivitiesFlow().collect { activities ->
                        _activitiesState.value = UiState.Success(activities)
                    }
                } catch (e: Exception) {
                    _activitiesState.value = UiState.Error("Failed to load activities: ${e.message}")
                }
            }
        }

        fun deleteActivity(activityId: String) {
            viewModelScope.launch {
                try {
                    val result = activityRepository.deleteActivity(activityId)

                    result.onSuccess {
                        Log.d("HomeViewModel", "Activity deleted successfully: $activityId")
                    }.onFailure { error ->
                        Log.e("HomeViewModel", "Failed to delete activity: ${error.message}", error)
                    }
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Exception when deleting activity: ${e.message}", e)
                }
            }
        }

        fun toggleSubActivityStatus(
            activityId: String,
            subActivityId: String,
            currentStatus: String,
        ) {
            viewModelScope.launch {
                try {
                    val newStatus = if (currentStatus == "pending") "completed" else "pending"

                    _activitiesState.value.let { state ->
                        if (state is UiState.Success) {
                            val updatedActivities =
                                state.data.map { activity ->
                                    if (activity.id == activityId) {
                                        val updatedSubActivities =
                                            activity.subActivities.map { subActivity ->
                                                if (subActivity.id == subActivityId) {
                                                    subActivity.copy(status = newStatus)
                                                } else {
                                                    subActivity
                                                }
                                            }
                                        activity.copy(subActivities = updatedSubActivities)
                                    } else {
                                        activity
                                    }
                                }
                            _activitiesState.value = UiState.Success(updatedActivities)
                        }
                    }

                    subActivityRepository.updateSubActivityStatus(activityId, subActivityId, newStatus)

                    checkAndUpdateActivityStatus(activityId)

                    // Adicione esta linha para verificar troféus após atualizar o status
                    checkForTrophies()
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Error toggling subactivity status: ${e.message}", e)
                }
            }
        }

        private fun checkAndUpdateActivityStatus(activityId: String) {
            viewModelScope.launch {
                when (val state = _activitiesState.value) {
                    is UiState.Success -> {
                        val activities = state.data
                        val activity = activities.find { it.id == activityId } ?: return@launch

                        // Verificar se todas as subatividades estão completas
                        val allSubactivitiesCompleted =
                            activity.subActivities.isNotEmpty() &&
                                activity.subActivities.all { it.status == "completed" }

                        // Atualizar status da atividade se necessário
                        if (allSubactivitiesCompleted && activity.status != "completed") {
                            // Adicione completionDate quando a atividade for completada
                            val updates =
                                mapOf(
                                    "status" to "completed",
                                    "completionDate" to System.currentTimeMillis(),
                                )
                            activityRepository.updateActivityStatus(activityId, "completed", updates)
                        } else if (!allSubactivitiesCompleted && activity.status == "completed") {
                            // Remova completionDate quando a atividade voltar a pending
                            val updates =
                                mapOf(
                                    "status" to "pending",
                                    "completionDate" to null,
                                )
                            activityRepository.updateActivityStatus(activityId, "pending", updates)
                        }
                    }

                    else -> {} // Não fazer nada para outros estados
                }
            }
        }

        // Atualizar filtro selecionado
        fun updateFilter(filter: String) {
            _selectedFilter.value = filter
        }

        // Atualizar query de busca
        fun updateSearchQuery(query: String) {
            searchQuery = query
        }

        // Formatar data para exibição
        fun formatDate(timestamp: Long?): String {
            if (timestamp == null) return "No due date"
            val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        private fun checkForTrophies() {
            viewModelScope.launch {
                try {
                    // Obter todas as atividades para verificação de troféus
                    when (val state = _activitiesState.value) {
                        is UiState.Success -> {
                            val activities = state.data
                            val completedActivities = activities.filter { it.status == "completed" }

                            val currentTrophies = trophiesRepository.getTrophies()
                            val updatedTrophies =
                                checkAchievements(currentTrophies, activities, completedActivities)

                            if (updatedTrophies != currentTrophies) {
                                trophiesRepository.saveTrophies(updatedTrophies)

                                val newlyAchieved =
                                    updatedTrophies.filter { trophy ->
                                        trophy.dateAchieved != null && currentTrophies.find { it.id == trophy.id }?.dateAchieved == null
                                    }

                                newlyAchieved.forEach { trophy ->
                                    Log.d("HomeViewModel", "Novo troféu conquistado: ${trophy.name}")
                                    // TODO: emitir um evento para notificar o usuário
                                }
                            }
                        }

                        else -> {} // Não faz nada para outros estados
                    }
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Erro ao verificar troféus: ${e.message}", e)
                }
            }
        }

        private fun checkAchievements(
            currentTrophies: List<Trophy>,
            allActivities: List<Activity>,
            completedActivities: List<Activity>,
        ): List<Trophy> {
            val mutableTrophies = currentTrophies.toMutableList()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Verificar troféu #1: Primeira atividade concluída
            val firstTaskTrophy = mutableTrophies.find { it.id == 1 }
            if (firstTaskTrophy?.dateAchieved == null && completedActivities.isNotEmpty()) {
                val index = mutableTrophies.indexOfFirst { it.id == 1 }
                if (index != -1) {
                    firstTaskTrophy?.copy(dateAchieved = today)?.let { mutableTrophies[index] = it }
                }
            }

            // Verificar troféu #3: 10 atividades concluídas
            val tenTasksTrophy = mutableTrophies.find { it.id == 3 }
            if (tenTasksTrophy?.dateAchieved == null && completedActivities.size >= 10) {
                val index = mutableTrophies.indexOfFirst { it.id == 3 }
                if (index != -1) {
                    tenTasksTrophy?.let { mutableTrophies[index] = it.copy(dateAchieved = today) }
                }
            }

            // Outras verificações de troféus...
            // Implemente lógica simplificada para outros troféus

            return mutableTrophies
        }
    }

// Estado da UI
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()

    data class Success<T>(val data: T) : UiState<T>()

    data class Error(val message: String) : UiState<Nothing>()
}
