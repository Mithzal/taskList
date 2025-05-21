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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.td2.model.Task
import com.example.td2.navigation.NavRoutes

class AddTaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddTaskScreen(navController = rememberNavController())
        }
    }
}

@Composable
fun AddTaskScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column {
        TextField(
            value=title,
            onValueChange = {title = it},
            label = { Text("Titre de la tâche") }

        )
        TextField(
            value=description,
            onValueChange = {description = it},
            label = { Text("Description de la tâche") }

        )
        Row {
            Button(onClick = {navController.navigate(NavRoutes.MAIN_SCREEN.route)}) { Text(text="Cancel")}
            Button(onClick ={
                val progress = 0.1f + (0.5f - 0.1f) * kotlin.random.Random.nextFloat()
                val newTask= Task(title, description, false, progress )
                tasks.add(newTask)
                navController.navigate(NavRoutes.MAIN_SCREEN.route)
            } ) {Text(text="Add") }
        }
    }

}