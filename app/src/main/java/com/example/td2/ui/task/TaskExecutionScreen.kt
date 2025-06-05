package com.example.td2.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.td2.LocalApp
import com.example.td2.data.local.Task
import kotlinx.coroutines.*
import com.example.td2.TaskApplication
import com.example.td2.ui.viewmodel.TaskListViewModel
import com.example.td2.ui.viewmodel.TaskListViewModelFactory

@Composable
fun TaskExecutionScreen() {
    val viewModel: TaskListViewModel = viewModel(
        factory = TaskListViewModelFactory(LocalApp.current.let { (it as TaskApplication).container.taskRepository })
    )

    val tasksState = remember { mutableStateListOf<Task>()}
    val tasksList by viewModel.tasks.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val isRunning = remember { mutableStateOf(false) }
    val isPaused = remember { mutableStateOf(false) }

    LaunchedEffect(tasksList) {
        tasksState.clear()
        tasksState.addAll(tasksList)
    }

    // Fonction locale pour mettre à jour les tâches
    val updateTask = { task: Task ->
        scope.launch {
            viewModel.updateTask(task)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (task in tasksState) {
            Text(task.title)
            LinearProgressIndicator(
                progress = task.progress,
                modifier = Modifier.padding(16.dp)
            )
        }

        Button(onClick = {
            isPaused.value = !isPaused.value
            if (!isPaused.value && !isRunning.value) {
                scope.launch {
                    runningAll(tasksState, isPaused, updateTask)
                }
                isRunning.value = true
            }
        }) {
            Text(if (isPaused.value) "Pause" else "Start")
        }

        Button(onClick = {
            resetAll(tasksState)
            tasksState.forEach { updateTask(it) }
            isPaused.value = false
            isRunning.value = false
        }) {
            Text("Reset")
        }
    }
}

suspend fun runningAll(
    tasksState: MutableList<Task>,
    isPaused: State<Boolean>,
    updateTask: (Task) -> Job
) {
    coroutineScope {
        tasksState.forEach { task ->
            launch {
                while (!task.isCompleted && isActive) {
                    if (!isPaused.value) { // Exécution quand NON pausé
                        delay(100)
                        task.isCompleted = run(task, isPaused, updateTask)
                    } else {
                        delay(100)
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

suspend fun run(
    task: Task,
    isPaused: State<Boolean>,
    updateTask: (Task) -> Job
): Boolean {
    var i = task.progress
    while (i < 1f) {
        delay(500)
        if (!isPaused.value) { // Progression quand NON pausé
            i += task.progressionSpeed
            task.progress = i
            updateTask(task)
        }
    }

    task.progress = 1f
    task.isCompleted = true
    updateTask(task)
    return true
}