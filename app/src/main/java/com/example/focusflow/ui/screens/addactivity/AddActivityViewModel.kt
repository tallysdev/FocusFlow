// ui/screens/addactivity/AddActivityViewModel.kt
package com.example.focusflow.ui.screens.addactivity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusflow.domain.model.Activity
import com.example.focusflow.domain.repository.IActivityRepository
import com.example.focusflow.domain.repository.ISubActivityRepository
import com.example.focusflow.domain.repository.ISubtaskGeneratorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddActivityViewModel
    @Inject
    constructor(
        private val activityRepository: IActivityRepository,
        private val subActivityRepository: ISubActivityRepository,
        private val subtaskGeneratorRepository: ISubtaskGeneratorRepository,
    ) : ViewModel() {
        private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
        val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

        private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
        val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

        fun saveActivity(activity: Activity) {
            viewModelScope.launch {
                try {
                    _saveState.value = SaveState.Saving
                    Log.d("AddActivityViewModel", "Saving activity: $activity")

                    val result = activityRepository.addActivity(activity)

                    result.onSuccess { activityId ->
                        Log.d("AddActivityViewModel", "Activity saved with ID: $activityId")
                        _saveState.value = SaveState.Success
                    }.onFailure { error ->
                        Log.e("AddActivityViewModel", "Error saving activity: ${error.message}", error)
                        _saveState.value = SaveState.Error("Failed to save: ${error.message}")
                    }
                } catch (e: Exception) {
                    Log.e("AddActivityViewModel", "Exception in saveActivity: ${e.message}", e)
                    _saveState.value = SaveState.Error("Exception: ${e.message}")
                }
            }
        }

        fun generateSubtasks(
            title: String,
            category: String,
            description: String,
        ) {
            if (title.isBlank()) {
                _generationState.value =
                    GenerationState.Error("Por favor, forneça um título para a tarefa")
                return
            }

            viewModelScope.launch {
                _generationState.value = GenerationState.Loading

                subtaskGeneratorRepository.generateSubtasks(title, category, description)
                    .onSuccess { subtasks ->
                        _generationState.value = GenerationState.Success(subtasks)
                    }
                    .onFailure { error ->
                        _generationState.value =
                            GenerationState.Error(error.message ?: "Falha ao gerar subtarefas")
                    }
            }
        }

        // Resetar o estado de geração
        fun resetGenerationState() {
            _generationState.value = GenerationState.Idle
        }
    }

// Estado para rastrear o processo de salvamento
sealed class SaveState {
    object Idle : SaveState()

    object Saving : SaveState()

    object Success : SaveState()

    data class Error(val message: String) : SaveState()
}

sealed class GenerationState {
    object Idle : GenerationState()

    object Loading : GenerationState()

    data class Success(val subtasks: List<String>) : GenerationState()

    data class Error(val message: String) : GenerationState()
}
