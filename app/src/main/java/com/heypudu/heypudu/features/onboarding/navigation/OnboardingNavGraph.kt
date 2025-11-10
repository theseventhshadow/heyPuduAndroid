package com.heypudu.heypudu.features.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.heypudu.heypudu.features.onboarding.ui.CreateProfileScreen
import com.heypudu.heypudu.features.onboarding.ui.EmailVerificationScreen
import com.heypudu.heypudu.features.onboarding.ui.GreetingScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heypudu.heypudu.features.onboarding.ui.LoginScreen

/*
 --- RUTAS DEL ONBOARDING ---
*/
object OnboardingRoutes {
    const val GRAPH = "onboarding_graph"
    const val GREETING = "greeting"
    const val CREATE_PROFILE = "create_profile"
    const val EMAIL_VERIFICATION = "email_verification"
    const val LOGIN = "login"
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
                onLoginClick = {
                    navController.navigate(OnboardingRoutes.LOGIN) {
                        popUpTo(OnboardingRoutes.GREETING) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                // Esto es para usuarios nuevos
                onProfileCreated = {
                    navController.navigate(OnboardingRoutes.CREATE_PROFILE) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(OnboardingRoutes.LOGIN) {
            LoginScreen(navController)
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
            val createProfileViewModel: com.heypudu.heypudu.features.onboarding.viewmodel.CreateProfileViewModel = viewModel()
            EmailVerificationScreen(
                viewModel = createProfileViewModel,
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