package com.example.td2.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.td2.model.Task
import com.example.td2.tasks
import kotlinx.coroutines.*



@Composable
fun TaskExecutionScreen() {
    // Utiliser mutableStateListOf au lieu de mutableStateOf pour une liste
    val tasksState = remember { mutableStateListOf<Task>().apply {
        addAll(tasks)
    }}
    val scope = rememberCoroutineScope()
    val isRunning = remember { mutableStateOf(false) }
    val isPaused = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Important: itérer sur tasksState et non sur tasks
        for (task in tasksState) {
            Text(task.title)
            LinearProgressIndicator(
                progress = { task.progress },
                modifier = Modifier.padding(16.dp)
            )
        }

        Button(onClick = {
            if (isPaused.value) {
                isPaused.value = false

            } else {
                if ( !isRunning.value){//on ne veut pas re-run le lancement des tâches
                    scope.launch {
                        runningAll(tasksState, isPaused)
                    }
                    isRunning.value = true
                }
                isPaused.value = true

            }
        }) {
            Text(if (isPaused.value) "Pause" else "Start")
        }

        Button(onClick = {
            resetAll(tasksState)
            isPaused.value = false
            isRunning.value = false
        }) {
            Text("Reset")
        }
    }
}


suspend fun runningAll(tasksState: MutableList<Task>, isPaused:State<Boolean>) {
    coroutineScope {
        tasksState.forEach { task ->
            launch {
                while (!task.isCompleted && isActive) {
                    if (isPaused.value) {
                        delay(100) // Attendre un peu avant de vérifier à nouveau
                        task.isCompleted = run(task, isPaused)
                    }

                }
            }
        }
    }
}

fun resetAll(tasksState: MutableList<Task>) {
    tasksState.forEach { task ->
        task.progress = 0f
        task.isCompleted = false
    }
}

suspend fun run(task: Task, isPaused: State<Boolean>): Boolean {
    var i = 0f
    while (i < 1f) {

        delay(500)
        if (isPaused.value) {
            i += task.progressionSpeed
            // Cette mise à jour déclenchera une recomposition si task.progress est observable
            task.progress = i
        }
    }
    return true
}