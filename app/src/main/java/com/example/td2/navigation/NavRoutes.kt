package com.example.td2.navigation

import android.net.Uri

// Créez un fichier séparé NavRoutes.kt
enum class NavRoutes(val route: String) {
    MAIN_SCREEN("mainScreen"),
    ADD_TASK("addTaskScreen"),
    TASK_DETAIL("taskDetailScreen/{title}/{description}/{isCompleted}"),
    PROGRESS("progress");

    // Méthodes utilitaires pour les routes avec paramètres
    fun createRoute(title: String, description: String, isCompleted: Boolean): String {
        return when (this) {
            TASK_DETAIL -> "taskDetailScreen/${Uri.encode(title)}/${Uri.encode(description)}/$isCompleted"
            else -> route
        }
    }
}