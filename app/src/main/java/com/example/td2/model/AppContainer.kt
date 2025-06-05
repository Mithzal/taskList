package com.example.td2.model


import android.content.Context
import com.example.td2.data.local.ListDatabase
import com.example.td2.repository.OfflineTaskRepository
import com.example.td2.repository.TasksRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {

    val taskRepository: TasksRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val taskRepository: TasksRepository by lazy {
        OfflineTaskRepository(ListDatabase.getDatabase(context).taskDao())
    }
}
