package com.example.td2.data

import com.example.td2.data.local.Task
import com.example.td2.ui.task.TaskUiState

data class TaskDetails(
    val id : Int =0,
    val name : String = "",
    val description : String = "",
    val isCompleted : Boolean = false,
    val progressionSpeed : Float = 0f
)

fun TaskDetails.toTask() : Task = Task(
    id = id,
    title = name,
    description = description,
    isCompleted = isCompleted,
    progressionSpeed = progressionSpeed
)

fun Task.toTaskDetails(): TaskDetails = TaskDetails(
    id = id,
    name = title,
    description = description,
    isCompleted = isCompleted,
    progressionSpeed = progressionSpeed
)

fun Task.toTaskUiState(isEntryValid: Boolean = false): TaskUiState = TaskUiState(
    taskDetails = this.toTaskDetails(),
    isEntryValid = isEntryValid
)
