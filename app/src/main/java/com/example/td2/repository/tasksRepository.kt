package com.example.td2.repository

import com.example.td2.data.local.Task
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

    fun getAllTasksStream() : Flow<List<Task>>

    fun getTaskStream(taskId: Int) : Flow<Task?>

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)


}