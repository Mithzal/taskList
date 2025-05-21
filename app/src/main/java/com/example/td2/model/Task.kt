package com.example.td2.model

import androidx.compose.runtime.mutableFloatStateOf

data class Task(
    val title: String,
    val description: String,
    var isCompleted: Boolean = false,
    val progressionSpeed: Float
) {
    // Rendre progress observable avec mutableStateOf
    private val _progress = mutableFloatStateOf(0f)
    var progress: Float
        get() = _progress.floatValue
        set(value) { _progress.floatValue = value }
}