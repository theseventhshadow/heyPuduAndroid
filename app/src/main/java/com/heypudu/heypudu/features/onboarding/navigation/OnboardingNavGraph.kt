package com.heypudu.heypudu.features.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.heypudu.heypudu.features.onboarding.ui.GreetingScreen
import com.heypudu.heypudu.navigation.AppRoutes

// Definimos las rutas específicas de este flujo
object OnboardingRoutes {
    const val GRAPH = "onboarding_graph" // Ruta para todo este sub-grafo
    const val GREETING = "greeting"
}

// Esta es una "extension function" que añade rutas a un NavGraphBuilder
fun NavGraphBuilder.onboardingGraph(navController: NavHostController) {
    // 1. Creamos un grafo anidado
    navigation(
        startDestination = OnboardingRoutes.GREETING, // Pantalla de inicio de ESTE grafo
        route = OnboardingRoutes.GRAPH
    ) {
        // 2. Definimos las pantallas DENTRO de este grafo
        composable(route = OnboardingRoutes.GREETING) {
            GreetingScreen(
                onProfileClick = {
                    // Navega a una ruta FUERA de este grafo (al grafo de perfil)
                    navController.navigate(AppRoutes.PROFILE_GRAPH)
                }
            )
        }

        // Si tuvieras una pantalla de "Tutorial", la añadirías aquí
        // composable(route = "tutorial") { ... }
    }
}
