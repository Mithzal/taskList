package com.example.td2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.td2.data.local.Task
import com.example.td2.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskListViewModel(private val repository: TasksRepository) : ViewModel() {
    val tasks: Flow<List<Task>> = repository.getAllTasksStream()
    fun updateTask(task: Task) {
        // Met à jour la tâche dans le repository
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
    fun getTaskById(id: String): Flow<Task?> {
        return repository.getTaskStream(id.toInt())
    }
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}

class TaskListViewModelFactory(private val repository: TasksRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
