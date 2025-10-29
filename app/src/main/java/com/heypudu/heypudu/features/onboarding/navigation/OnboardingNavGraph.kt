package com.heypudu.heypudu.features.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.heypudu.heypudu.features.onboarding.ui.CreateProfileScreen
import com.heypudu.heypudu.features.onboarding.ui.EmailVerificationScreen
import com.heypudu.heypudu.features.onboarding.ui.GreetingScreen

/*
 --- RUTAS DEL ONBOARDING ---
*/
object OnboardingRoutes {
    const val GRAPH = "onboarding_graph"
    const val GREETING = "greeting"
    const val CREATE_PROFILE = "create_profile"
    // 1. Nueva ruta para la pantalla de verificación
    const val EMAIL_VERIFICATION = "email_verification"
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
        // Pantalla de bienvenida
        composable(route = OnboardingRoutes.GREETING) {
            GreetingScreen(
                // Esto es para usuarios que ya tienen cuenta (ej. Login)
                // Lo dejamos apuntando al grafo principal por ahora.
                onContinueClick = {
                    navController.navigate("main_graph") {
                        popUpTo(OnboardingRoutes.GRAPH) { inclusive = true }
                    }
                },
                // Esto es para usuarios nuevos
                onProfileCreated = {
                    navController.navigate(OnboardingRoutes.CREATE_PROFILE)
                }
            )
        }

        // Pantalla de creación de perfil
        composable(OnboardingRoutes.CREATE_PROFILE) {
            // 2. Corregimos el parámetro y el destino de la navegación
            CreateProfileScreen(
                onNavigateToVerification = {
                    navController.navigate(OnboardingRoutes.EMAIL_VERIFICATION)
                }
            )
        }

        // 3. Añadimos la nueva pantalla al gráfico de navegación
        composable(OnboardingRoutes.EMAIL_VERIFICATION) {
            EmailVerificationScreen(
                onGoToMainApp = {
                    // Navega a la pantalla de inicio de sesión (GreetingScreen)
                    navController.navigate(OnboardingRoutes.GREETING) {
                        popUpTo(OnboardingRoutes.GRAPH) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}