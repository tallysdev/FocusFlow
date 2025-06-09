package com.example.focusflow.ui.screens.home

data class Task(val id: Int, val title: String, var done: Boolean = false)

data class TaskGroup(val id: Int, val title: String, val tasks: List<Task>)

class HomeViewModel : ViewModel() {
    private val _groups = mutableStateListOf<TaskGroup>()
    val groups: List<TaskGroup> = _groups

    init {
        _groups.addAll(listOf(
            TaskGroup(1, "Event", listOf(Task(1, "Child Activity 1"), Task(2, "Child Activity 2"))),
            TaskGroup(2, "Work", listOf(Task(3, "Catan Tournament"))),
            TaskGroup(3, "Travel", listOf(Task(4, "Music Festival")))
        ))
    }

    fun toggle(task: Task) {
        task.done = !task.done
        _groups.replaceAll { grp ->
            grp.copy(tasks = grp.tasks.map { if (it.id == task.id) it.copy(done = task.done) else it })
        }
    }
}
