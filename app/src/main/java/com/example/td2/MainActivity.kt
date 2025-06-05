package com.example.td2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.td2.ui.theme.Td2Theme
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.td2.data.local.Task
import com.example.td2.navigation.NavRoutes
import android.app.Application
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.td2.ui.task.TaskApplication
import com.example.td2.ui.viewmodel.TaskListViewModel
import com.example.td2.ui.viewmodel.TaskListViewModelFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.td2.navigation.AppNavigation
import kotlinx.coroutines.delay



val LocalApp = staticCompositionLocalOf<Application> {
    error("No Application provided")
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration plein écran
        enableEdgeToEdge()

        // Pour masquer complètement la barre de navigation
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            CompositionLocalProvider(LocalApp provides application) {
                Td2Theme {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TaskListViewModel = viewModel(
        factory = TaskListViewModelFactory(LocalApp.current.let { (it as TaskApplication).container.taskRepository })
    )
) {
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    val scrollState = rememberScrollState()
    val buttonVisible = remember { derivedStateOf { scrollState.value < 200 } }
    val showLimitMessage = remember { mutableStateOf(false) }


    LaunchedEffect(showLimitMessage.value) {
        if (showLimitMessage.value) {
            delay(3000)
            showLimitMessage.value = false
        }
    }



    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate(NavRoutes.PROGRESS.route) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Afficher le progrès")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { navController.navigate(NavRoutes.QUOTE.route) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Quote of the day !")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            tasks.forEach { task ->
                TaskItem(
                    task = task,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    navController = navController,
                    viewModel = viewModel
                )
            }

            Spacer(modifier = Modifier.height(80.dp)) // Espace pour le bouton flottant
        }


        AnimatedVisibility(
            // Le bouton n'est visible que si l'utilisateur est proche du haut de la liste
            visible = buttonVisible.value,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp),
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            // Bouton avec élévation pour effet de profondeur
            ElevatedButton(
                onClick = {
                    // Vérifier le nombre de tâches avant de naviguer
                    if (tasks.size >= 20) {
                        showLimitMessage.value = true
                    } else {
                        navController.navigate(NavRoutes.ADD_TASK.route)
                    }
                },
                modifier = Modifier
                    .padding(
                        end = 16.dp,
                        bottom = 16.dp
                    ) // Le padding doit être appliqué avant la taille
                    .size(56.dp), // Taille fixe pour un bouton parfaitement rond
                shape = CircleShape, // Forme circulaire pour le bouton
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 6.dp
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp) // Supprimer le padding interne par défaut
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter une tâche",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White // Assurer que l'icône est visible
                )
            }
        }
        AnimatedVisibility(
            visible = showLimitMessage.value,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .zIndex(10f), // Garantit que le message est toujours au premier plan

            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red.copy(alpha = 0.8f))
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Vous ne pouvez pas ajouter plus de 20 tâches.",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }


}


@Composable
fun TaskItem(
    task: Task, modifier: Modifier = Modifier, navController: NavController,
    viewModel: TaskListViewModel = viewModel(
        factory = TaskListViewModelFactory(LocalApp.current.let { (it as TaskApplication).container.taskRepository })
    )
) {
    var modified by remember { mutableStateOf(task.isCompleted) }

    Row {
        Button(
            onClick = {
                val id = task.id

                navController.navigate(NavRoutes.TASK_DETAIL.createRoute(id))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = animateColorAsState(
                    targetValue = if (modified) Color(0xFFB2FF59)
                    else Color(0xFFFF8A65),
                    animationSpec = tween(durationMillis = 300)
                ).value
            )
        ) {
            Checkbox(checked = modified, onCheckedChange = {
                modified = it
                viewModel.updateTask(task.copy(isCompleted = it))
            })
            Text(text = task.title)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Td2Theme {
        AppNavigation()
    }
}
