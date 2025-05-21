package com.example.td2

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.td2.model.Task
import com.example.td2.navigation.DetailScreen
import com.example.td2.navigation.NavRoutes
import com.example.td2.navigation.TaskExecutionScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "mainScreen"){
        composable(NavRoutes.MAIN_SCREEN.route) {TaskListScreen(navController = navController)}
        composable(NavRoutes.ADD_TASK.route){ AddTaskScreen(navController = navController) }
        composable(NavRoutes.TASK_DETAIL.route, arguments = listOf(
            navArgument("title") { type = NavType.StringType },
            navArgument("description") { type = NavType.StringType },
            navArgument("isCompleted") { type = NavType.StringType }
        )) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val isCompleted = backStackEntry.arguments?.getString("isCompleted")?.toBoolean() == true
            DetailScreen(title, description, isCompleted, navController = navController)
        }
        composable(NavRoutes.PROGRESS.route) { TaskExecutionScreen() }

    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Td2Theme {
                AppNavigation()

            }
        }
    }
}

var tasks = mutableListOf(
    Task("Travailler", "BOSSE !!!", false,0.1f),
    Task("Courses", "Acheter oeufs, lait et farine ", true, 0.2f),
    Task("Sport", "Faire du vélo", false, 0.3f),
    Task("Ménage", "Faire la vaisselle", false, 0.1f),
    Task("Loisirs", "Regarder un film", true, 0.2f)
)


@Composable
fun TaskListScreen( modifier: Modifier = Modifier, navController: NavController) {

    Column(modifier = modifier
        .fillMaxSize()
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (task in tasks) {

            TaskItem(
                task, Modifier.align(Alignment.CenterHorizontally),
                navController
            )


        }
        Button(onClick = { navController.navigate(NavRoutes.ADD_TASK.route) }) {
            Text(text = "Ajouter une tâche")
        }
        Button(onClick = {navController.navigate(NavRoutes.PROGRESS.route)}) {
            Text("Afficher le progrès")
        }
    }

}

@Composable
fun TaskItem(task: Task, modifier: Modifier = Modifier, navController: NavController) {
    var modified by remember { mutableStateOf(task.isCompleted) }

    Row {
        Button(onClick = {
            val title = Uri.encode(task.title)
            val description = Uri.encode(task.description)
            val isCompleted = task.isCompleted
            navController.navigate(NavRoutes.TASK_DETAIL.createRoute(title, description, isCompleted))
        })
            
             {
            Checkbox(checked = modified, onCheckedChange = { modified = it })
            Text(text = task.title)
            Text(text = task.description)

            val image = if (modified) {
                painterResource(id = R.drawable.baseline_task_alt_24)
            } else {
                painterResource(id = R.drawable.baseline_task_alt_black)
            }
            Image(painter = image, contentDescription = null)
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

