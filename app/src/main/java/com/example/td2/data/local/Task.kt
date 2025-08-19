package com.example.td2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id :Int = 0,
    val title: String,
    val description: String,
    var isCompleted: Boolean = false,
    var deadlineDate : Long = System.currentTimeMillis()
)