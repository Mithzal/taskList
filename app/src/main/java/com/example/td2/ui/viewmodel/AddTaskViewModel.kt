package com.example.td2.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.td2.data.TaskDetails
import com.example.td2.repository.TasksRepository
import com.example.td2.ui.task.TaskUiState
import com.example.td2.data.toTask
import kotlinx.coroutines.launch

class AddTaskViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    var taskUiState by mutableStateOf(TaskUiState())
        private set

    // Ajoute un état pour notifier la sauvegarde
    var taskSaved by mutableStateOf(false)
        private set

    fun updateUiState(taskDetails: TaskDetails){
        taskUiState = TaskUiState(taskDetails= taskDetails, isEntryValid = validateInput(taskDetails))
    }

    fun saveTask() {
        if (validateInput()) {
            viewModelScope.launch {
                tasksRepository.insertTask(taskUiState.taskDetails.toTask())
                taskSaved = true
            }
        }
    }

    private fun validateInput(uiState : TaskDetails = taskUiState.taskDetails) : Boolean{
        return uiState.name.isNotBlank() && uiState.description.isNotBlank()
                && uiState.progressionSpeed in 0f..1f
    }

}