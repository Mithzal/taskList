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
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.td2.data.local.Task
import com.example.td2.ui.task.DetailScreen
import com.example.td2.navigation.NavRoutes
import com.example.td2.ui.task.TaskExecutionScreen
import com.example.td2.ui.quote.quoteScreen
import android.app.Application
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import com.example.td2.ui.task.AddTaskScreen
import com.example.td2.ui.task.TaskApplication
import com.example.td2.ui.viewmodel.TaskListViewModel
import com.example.td2.ui.viewmodel.TaskListViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "mainScreen") {
        composable(NavRoutes.MAIN_SCREEN.route) {
            TaskListScreen(navController = navController)
        }
        composable(NavRoutes.ADD_TASK.route) { AddTaskScreen(navController = navController) }
        composable(NavRoutes.TASK_DETAIL.route, arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        )) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "0"
            DetailScreen(id, navController = navController)
        }
        composable(NavRoutes.PROGRESS.route) { TaskExecutionScreen() }
        composable(NavRoutes.QUOTE.route) { quoteScreen(navController = navController) }
    }
}

val LocalApp = staticCompositionLocalOf<Application> {
    error("No Application provided")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Fournit l'instance Application à la composition
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (task in tasks) {
            TaskItem(
                task,
                Modifier.align(Alignment.CenterHorizontally),
                navController,
                viewModel = viewModel
            )
        }
        Button(onClick = { navController.navigate(NavRoutes.ADD_TASK.route) }) {
            Text(text = "Ajouter une tâche")
        }
        Button(onClick = { navController.navigate(NavRoutes.PROGRESS.route) }) {
            Text("Afficher le progrès")
        }
        Button(onClick = { navController.navigate(NavRoutes.QUOTE.route) }) {
            Text("Quote of the day !")
        }
    }
}

@Composable
fun TaskItem(task: Task, modifier: Modifier = Modifier, navController: NavController,
             viewModel: TaskListViewModel = viewModel(
    factory = TaskListViewModelFactory(LocalApp.current.let { (it as TaskApplication).container.taskRepository })
)) {
    var modified by remember { mutableStateOf(task.isCompleted) }

    Row {
        Button(
            onClick = {
                val id = task.id

                navController.navigate(NavRoutes.TASK_DETAIL.createRoute(id))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (modified) androidx.compose.ui.graphics.Color(0xFFB2FF59)
                else androidx.compose.ui.graphics.Color(0xFFFF8A65)
            )
        ) {
            Checkbox(checked = modified, onCheckedChange = { modified = it
                viewModel.updateTask(task.copy(isCompleted = it))})
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

// ViewModel pour la liste des tâches

