package com.heypudu.heypudu.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.heypudu.heypudu.features.onboarding.OnboardingRoutes
import com.heypudu.heypudu.features.onboarding.onboardingGraph
import com.heypudu.heypudu.features.profile.ProfileRoutes
import com.heypudu.heypudu.features.profile.profileGraph

/**
 * Ahora las rutas principales son los GRAFOS de cada funcionalidad.
 */
object AppRoutes {
    const val ONBOARDING_GRAPH = OnboardingRoutes.GRAPH
    const val PROFILE_GRAPH = ProfileRoutes.GRAPH
    // Si tuvieras autenticación, sería: const val AUTH_GRAPH = "auth_graph"
}

/**
 * El NavHost principal ahora está súper limpio.
 * Solo conoce los grafos, no las pantallas individuales.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // La app empieza en el flujo de "onboarding"
        startDestination = AppRoutes.ONBOARDING_GRAPH
    ) {
        // Registra el grafo de onboarding
        onboardingGraph(navController)

        // Registra el grafo de perfil
        profileGraph(navController)

        // Cuando añadas la funcionalidad de autenticación:
        // authGraph(navController)

        // Cuando añadas la mensajería:
        // messagingGraph(navController)
    }
}
