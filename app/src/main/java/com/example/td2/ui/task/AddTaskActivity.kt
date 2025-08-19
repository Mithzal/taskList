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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val taskDetails: TaskDetails = TaskDetails(),
    val isEntryValid: Boolean = false
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavController,
    viewModel: AddTaskViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val taskUiState = viewModel.taskUiState
    val datePickerState = rememberDatePickerState()
    val selectedDateMillis = remember { mutableStateOf<Long?>(null) }
    val showDatePicker = remember { mutableStateOf(false) }
    val hasChanged = remember { mutableStateOf(false) }


    // Observe l'état de sauvegarde pour naviguer après ajout
    if (viewModel.taskSaved) {
        LaunchedEffect(Unit) {
            navController.navigate(NavRoutes.MAIN_SCREEN.route)
        }
    }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { timestamp ->
            selectedDateMillis.value = timestamp
            // Mettre à jour le modèle avec la date sélectionnée
            viewModel.updateUiState(taskUiState.taskDetails.copy(deadlineDate = timestamp))
        }
    }


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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
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
                            viewModel.updateUiState(taskUiState.taskDetails.copy(name = it));
                            hasChanged.value = true
                        },
                        label = { Text("Titre de la tâche") },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    OutlinedTextField(
                        value = taskUiState.taskDetails.description,
                        onValueChange = {
                            viewModel.updateUiState(taskUiState.taskDetails.copy(description = it));
                            hasChanged.value = true
                        },
                        label = { Text("Description de la tâche") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    OutlinedButton(
                        onClick = { showDatePicker.value = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (selectedDateMillis.value == null)
                                "Sélectionner une date limite"
                            else {
                                val date = Date(selectedDateMillis.value!!)
                                SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE).format(date)
                            }
                        )
                    }
                    if (showDatePicker.value) {

                        DatePickerDialog(
                            onDismissRequest = { showDatePicker.value = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { timestamp ->
                                        viewModel.updateUiState(
                                            taskUiState.taskDetails.copy(
                                                deadlineDate = timestamp
                                            )
                                        );
                                        hasChanged.value = true
                                    }
                                    showDatePicker.value = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker.value = false }) {
                                    Text("Annuler")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }


                }
            }
            val currentDateValue = selectedDateMillis.value

            if (currentDateValue != null && currentDateValue < System.currentTimeMillis() && hasChanged.value) {
                Text(
                    text = "La deadline ne peut pas être antérieur à aujourd'hui.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (!taskUiState.isEntryValid && hasChanged.value) {
                Text(
                    text = "Veuillez remplir tous les champs correctement.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                //ne rien afficher si l'entrée est valide ou qu'il n'y a pas eu de changement

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