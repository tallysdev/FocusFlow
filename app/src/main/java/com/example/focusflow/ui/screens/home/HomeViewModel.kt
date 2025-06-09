package com.example.focusflow.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

data class Task(val id: Int, val title: String, val dueDate: String, var done: Boolean = false)

data class TaskGroup(val id: Int, val dueDate: String, val category: String, val title: String, val tasks: List<Task>)



class HomeViewModel : ViewModel() {
    private val _groups = mutableStateListOf<TaskGroup>()
    val groups: List<TaskGroup> = _groups

    init {
        _groups.addAll(
            listOf(
                TaskGroup(1, "09/06", "Event", "Private Event", listOf(Task(1, "Child Activity 1", "10/06"), Task(2, "Child Activity 2", "10/06"))),
                TaskGroup(2, "09/06",  "Work", "Mobile project", listOf(Task(3, "Catan Tournament", "10/06"))),
                TaskGroup(3, "09/06",  "Travel", "Quest", listOf(Task(4, "Music Festival", "10/06")))
            )
        )
    }

    fun toggle(task: Task) {
        val updatedGroups = _groups.map { group ->
            val updatedTasks = group.tasks.map { t ->
                if (t.id == task.id) t.copy(done = !t.done) else t
            }
            group.copy(tasks = updatedTasks)
        }
        _groups.clear()
        _groups.addAll(updatedGroups)
    }
}
