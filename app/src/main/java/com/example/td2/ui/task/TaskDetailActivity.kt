package com.example.td2.ui.task


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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.runtime.getValue
import com.example.td2.navigation.NavRoutes


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

    if (context is ComponentActivity && task != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                Text("Cancel")
            }
            Button(
                onClick = {
                    // Stockez la référence à la tâche
                    val taskToDelete = task

                    // Naviguez d'abord
                    navController.navigate(NavRoutes.MAIN_SCREEN.route) {
                        // Effacer le back stack pour éviter de revenir à cet écran
                        popUpTo(NavRoutes.MAIN_SCREEN.route) { inclusive = true }
                    }

                    // Supprimez ensuite la tâche
                    if (taskToDelete != null) {
                        viewModel.deleteTask(taskToDelete)
                    }
                }
            ) {
                Text("Delete")
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    } else {
        // Afficher un message si la tâche est null
        Text(text = "Tâche non trouvée")
    }
}