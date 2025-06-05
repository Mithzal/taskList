package com.example.td2.ui.task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.td2.navigation.NavRoutes
import android.app.Application
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.td2.model.AppContainer
import com.example.td2.model.AppDataContainer
import com.example.td2.ui.viewmodel.AddTaskViewModel
import kotlin.random.Random
import com.example.td2.data.TaskDetails
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


class AddTaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddTaskScreen(navController = rememberNavController())
        }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavController,
    viewModel: AddTaskViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val taskUiState = viewModel.taskUiState

    // Observe l'état de sauvegarde pour naviguer après ajout
    if (viewModel.taskSaved) {
        LaunchedEffect(Unit) {
            navController.navigate(NavRoutes.MAIN_SCREEN.route)
        }
    }

    // État pour prévisualiser la vitesse de progression
    val progressionSpeed = remember { mutableStateOf(0.1f + (0.5f - 0.1f) * Random.nextFloat()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ajouter une tâche") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(NavRoutes.MAIN_SCREEN.route) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Informations de la tâche",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = taskUiState.taskDetails.name,
                        onValueChange = {
                            viewModel.updateUiState(taskUiState.taskDetails.copy(name = it))
                        },
                        label = { Text("Titre de la tâche") },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = taskUiState.taskDetails.description,
                        onValueChange = {
                            viewModel.updateUiState(taskUiState.taskDetails.copy(description = it))
                        },
                        label = { Text("Description de la tâche") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Vitesse de progression",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Vitesse: ${(progressionSpeed.value * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    LinearProgressIndicator(
                        progress = progressionSpeed.value,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { progressionSpeed.value = 0.1f + (0.5f - 0.1f) * Random.nextFloat() },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Régénérer")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                OutlinedButton(
                    onClick = { navController.navigate(NavRoutes.MAIN_SCREEN.route) }
                ) {
                    Text("Annuler")
                }

                Button(
                    onClick = {
                        viewModel.updateUiState(taskUiState.taskDetails.copy(progressionSpeed = progressionSpeed.value))
                        viewModel.saveTask()
                    },
                    enabled = taskUiState.isEntryValid,
                    modifier = Modifier.animateContentSize()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ajouter")
                }
            }
        }
    }
}