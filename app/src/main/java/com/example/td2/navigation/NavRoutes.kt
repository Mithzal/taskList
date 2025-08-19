package com.example.td2.navigation

// Créez un fichier séparé NavRoutes.kt
enum class NavRoutes(val route: String) {
    MAIN_SCREEN("mainScreen"),
    ADD_TASK("addTaskScreen"),
    TASK_DETAIL("taskDetailScreen/{id}"),
    QUOTE("quote");

    // Méthodes utilitaires pour les routes avec paramètres
    fun createRoute(id : Int): String {
        return when (this) {
            TASK_DETAIL -> "taskDetailScreen/${id}"
            else -> route
        }
    }
}