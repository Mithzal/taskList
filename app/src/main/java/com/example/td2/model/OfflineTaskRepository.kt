package com.example.td2.model

import kotlinx.coroutines.flow.Flow


class OfflineTaskRepository(private val taskDao: TaskDao) :TasksRepository {

    override fun getAllTasksStream() : Flow<List<Task>> = taskDao.getAllTasks()

    override fun getTaskStream(taskId: Int): Flow<Task?> = taskDao.getTaskById(taskId)

    override suspend fun insertTask(task: Task) = taskDao.insert(task)
    override suspend fun updateTask(task: Task) = taskDao.update(task)
    override suspend fun deleteTask(task: Task) = taskDao.delete(task)





}