package com.example.td2.ui.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.td2.LocalApp
import com.example.td2.R
import com.example.td2.ui.viewmodel.TaskListViewModelFactory
import com.example.td2.ui.viewmodel.TaskListViewModel
import com.example.td2.navigation.NavRoutes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    id: String,
    navController: NavController,
    viewModel: TaskListViewModel = viewModel(
        factory = TaskListViewModelFactory(LocalApp.current.let { (it as TaskApplication).container.taskRepository })
    )
) {
    val context = LocalContext.current
    val taskFlow = viewModel.getTaskById(id)
    val task by taskFlow.collectAsState(initial = null)
    var isDeleting by remember { mutableStateOf(false) }

    LaunchedEffect(isDeleting) {
        if (isDeleting && task != null) {
            delay(300) // Durée de l'animation
            viewModel.deleteTask(task!!)
            navController.navigate(NavRoutes.MAIN_SCREEN.route) {
                popUpTo(NavRoutes.MAIN_SCREEN.route) { inclusive = true }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails de la tâche") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = !isDeleting,
                        exit = fadeOut() + scaleOut()
                    ) {
                        IconButton(
                            onClick = { isDeleting = true },
                            modifier = Modifier.animateContentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Supprimer",
                                tint = Color.Red
                            )
                        }
                    }
                },
                modifier = Modifier.padding(top = 24.dp)

            )
        }
    ) { paddingValues ->
        // Animation du contenu principal
        AnimatedVisibility(
            visible = !isDeleting,
            exit = slideOutHorizontally { it } + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            if (context is ComponentActivity && task != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .animateContentSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Text(text = task!!.title)
                    Spacer(modifier = Modifier.weight(1f))

                    Text(text = Uri.decode(task!!.description))
                    Spacer(modifier = Modifier.weight(1f))

                    Spacer(modifier = Modifier.weight(0.5f))
                    val image = if (task!!.isCompleted) {
                        painterResource(id = R.drawable.baseline_task_alt_24)
                    } else {
                        painterResource(id = R.drawable.baseline_task_alt_black)
                    }
                    Image(painter = image, contentDescription = null)

                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Title: ${task!!.title}\nDescription: ${task!!.description}\nisCompleted: ${task!!.isCompleted}")
                        }
                        context.startActivity(intent)
                    }) {
                        Text(text = "Share")
                    }

                    Button(onClick = { navController.navigate(NavRoutes.MAIN_SCREEN.route) }) {
                        Text("Retour")
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                // Afficher un message si la tâche est null
                Text(text = "Tâche non trouvée")
            }
        }
    }

}