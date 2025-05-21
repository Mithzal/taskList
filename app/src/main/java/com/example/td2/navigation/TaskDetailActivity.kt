package com.example.td2.navigation


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.td2.R

class TaskDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val title = "titre"
            val description = "description"
            val isDone = false

            DetailScreen(title, description, isDone, navController = rememberNavController())
        }
    }
}

@Composable
fun DetailScreen(title : String = "default", description : String = "default", isDone :Boolean, navController: NavController) {
    val context = LocalContext.current
    if(context is ComponentActivity) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(text = title)
            Spacer(modifier = Modifier.weight(1f))

            Text(text = description)
            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.weight(0.5f))
            val image = if (isDone) {
                painterResource(id = R.drawable.baseline_task_alt_24)
            } else {
                painterResource(id = R.drawable.baseline_task_alt_black)
            }
            Image(painter = image, contentDescription = null)

            Button(onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Title: $title\nDescription: $description\nisCompleted: $isDone")
                }
                context.startActivity(intent)
            }) {
                Text(text = "Share")
            }

            Button(onClick = { navController.navigate(NavRoutes.MAIN_SCREEN.route) }) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

