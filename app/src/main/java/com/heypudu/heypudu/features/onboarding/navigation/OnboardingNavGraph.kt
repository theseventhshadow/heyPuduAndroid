package com.heypudu.heypudu.features.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.heypudu.heypudu.features.onboarding.ui.GreetingScreen
import com.heypudu.heypudu.navigation.AppRoutes
import com.heypudu.heypudu.features.onboarding.ui.CreateProfileScreen

/*
 --- RUTAS DEL ONBOARDING ---
*/
object OnboardingRoutes {
    const val GRAPH = "onboarding_graph"
    const val GREETING = "greeting"
    const val CREATE_PROFILE = "create_profile"

}

/*
 --- GRÁFICO DEL ONBOARDING ---
*/
fun NavGraphBuilder.onboardingGraph(navController: NavHostController) {
    // Creacion de un grafo anidado para el onboarding
    navigation(
        startDestination = OnboardingRoutes.GREETING,
        route = OnboardingRoutes.GRAPH
    ) {
        // Definicion de las pantallas del onboarding
        composable(route = OnboardingRoutes.GREETING) {
            GreetingScreen(
                onContinueClick = {
                    // Navega a la pantalla de perfil al hacer clic en el botón
                    navController.navigate(AppRoutes.PROFILE_GRAPH)
                },
                onProfileCreated = {
                    navController.navigate(OnboardingRoutes.CREATE_PROFILE)
                }
            )
        }

        composable(OnboardingRoutes.CREATE_PROFILE) {
            CreateProfileScreen(
                onProfileCreated = {
                    navController.navigate("main_graph") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
