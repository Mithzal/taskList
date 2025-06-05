package com.example.td2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.td2.TaskListScreen
import com.example.td2.ui.quote.quoteScreen
import com.example.td2.ui.task.AddTaskScreen
import com.example.td2.ui.task.DetailScreen
import com.example.td2.ui.task.TaskExecutionScreen


    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "mainScreen") {
            composable(NavRoutes.MAIN_SCREEN.route) {
                TaskListScreen(navController = navController)
            }
            composable(NavRoutes.ADD_TASK.route) { AddTaskScreen(navController = navController) }
            composable(
                NavRoutes.TASK_DETAIL.route, arguments = listOf(
                    navArgument("id") { type = NavType.StringType }
                )) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: "0"
                DetailScreen(id, navController = navController)
            }
            composable(NavRoutes.PROGRESS.route) { TaskExecutionScreen() }
            composable(NavRoutes.QUOTE.route) { quoteScreen(navController = navController) }
        }
    }
