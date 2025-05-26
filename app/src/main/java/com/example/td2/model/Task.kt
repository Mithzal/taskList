package com.example.td2.model

import androidx.compose.runtime.mutableFloatStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id :Int = 0,
    val title: String,
    val description: String,
    var isCompleted: Boolean = false,
    val progressionSpeed: Float)
    val progress: Float = 0f // Ajout d'un champ pour la progression
// {
//    // Rendre progress observable avec mutableStateOf
//    private val _progress = mutableFloatStateOf(0f)
//    var progress: Float
//        get() = _progress.floatValue
//        set(value) { _progress.floatValue = value }
//}

//todo : continuer
// https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room?hl=fr#8