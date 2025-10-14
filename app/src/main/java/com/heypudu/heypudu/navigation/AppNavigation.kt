package com.heypudu.heypudu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.heypudu.heypudu.features.onboarding.navigation.OnboardingRoutes
import com.heypudu.heypudu.features.onboarding.navigation.onboardingGraph
import com.heypudu.heypudu.features.profile.ProfileRoutes
import com.heypudu.heypudu.features.profile.profileGraph
import com.heypudu.heypudu.features.splash.ui.SplashScreen

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
    var showSplashScreen by remember { mutableStateOf(true) }

    if (showSplashScreen) {
        SplashScreen(
            onTimeout = {
                showSplashScreen = false
            }
        )
    } else {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = OnboardingRoutes.GRAPH
        ) {
            onboardingGraph(navController)
            profileGraph(navController)
        }
    }
}
