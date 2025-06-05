package com.example.td2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.td2.data.local.Task
import com.example.td2.navigation.NavRoutes
import com.example.td2.repository.TasksRepository
import androidx.lifecycle.viewModelScope
import android.app.Application
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import com.example.td2.model.AppContainer
import com.example.td2.model.AppDataContainer


class AddTaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddTaskScreen(navController = rememberNavController())
        }
    }
}

class AddTaskViewModel(private val tasksRepository: TasksRepository) : androidx.lifecycle.ViewModel() {
    var taskUiState by mutableStateOf(TaskUiState())
        private set

    // Ajoute un état pour notifier la sauvegarde
    var taskSaved by mutableStateOf(false)
        private set

    fun updateUiState(taskDetails: TaskDetails){
        taskUiState = TaskUiState(taskDetails= taskDetails, isEntryValid = validateInput(taskDetails))
    }

    fun saveTask() {
        if (validateInput()) {
            viewModelScope.launch {
                tasksRepository.insertTask(taskUiState.taskDetails.toTask())
                taskSaved = true
            }
        }
    }

    private fun validateInput(uiState : TaskDetails = taskUiState.taskDetails) : Boolean{
        return uiState.name.isNotBlank() && uiState.description.isNotBlank()
                && uiState.progressionSpeed in 0f..1f
    }

}

class TaskApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}




object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Récupérer l'application
        initializer {
            val application = (this[AndroidViewModelFactory.APPLICATION_KEY] as Application)
            val tasksRepository = (application as TaskApplication).container.taskRepository

            AddTaskViewModel(tasksRepository = tasksRepository)
        }
    }
}

data class TaskUiState(
    val taskDetails : TaskDetails = TaskDetails(),
    val isEntryValid : Boolean = false
)

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

@Composable
fun AddTaskScreen(
    navController: NavController,
    viewModel: AddTaskViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = AppViewModelProvider.Factory
    )
) {
    val taskUiState = viewModel.taskUiState

    // Observe l'état de sauvegarde pour naviguer après ajout
    if (viewModel.taskSaved) {
        LaunchedEffect(Unit) {
            navController.navigate(NavRoutes.MAIN_SCREEN.route)
        }
    }

    Column {
        TextField(
            value = taskUiState.taskDetails.name,
            onValueChange = {
                viewModel.updateUiState(taskUiState.taskDetails.copy(name = it))
            },
            label = { Text("Titre de la tâche") }
        )

        TextField(
            value = taskUiState.taskDetails.description,
            onValueChange = {
                viewModel.updateUiState(taskUiState.taskDetails.copy(description = it))
            },
            label = { Text("Description de la tâche") }
        )

        Row {
            Button(onClick = { navController.navigate(NavRoutes.MAIN_SCREEN.route) }) {
                Text(text = "Cancel")
            }

            Button(
                onClick = {
                    val progress = 0.1f + (0.5f - 0.1f) * kotlin.random.Random.nextFloat()
                    viewModel.updateUiState(taskUiState.taskDetails.copy(progressionSpeed = progress))
                    viewModel.saveTask()
                },
                enabled = taskUiState.isEntryValid
            ) {
                Text(text = "Add")
            }
        }
    }
}
