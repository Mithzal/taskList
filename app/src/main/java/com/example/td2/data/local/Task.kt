package com.example.td2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id :Int = 0,
    val title: String,
    val description: String,
    var isCompleted: Boolean = false,
    val progressionSpeed: Float,
    var progress: Float = 0f)