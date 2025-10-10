package com.heypudu.heypudu.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost // <-- Importación correcta
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heypudu.heypudu.greeting.GreetingScreen
import com.heypudu.heypudu.profile.Profile


/**
 * Objeto para definir las rutas de la app de forma segura.
 * Usar esto en lugar de strings ("profile") previene errores de tipeo.
 */
object AppRoutes {
    const val GREETING = "greeting"
    const val PROFILE = "profile"

    const val MESSAGES = "messages"
}

/**
 * Esta es la función Composable que construye el grafo de navegación.
 */
@Preview
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.GREETING // Define la pantalla de inicio
    ) {
        // Define la pantalla "greeting"
        composable(route = AppRoutes.GREETING) {
            // Le pasamos la lógica para navegar cuando se haga clic
            GreetingScreen(
                onProfileClick = {
                    navController.navigate(AppRoutes.PROFILE)
                }
            )
        }

        // Define la pantalla "profile"
        composable(route = AppRoutes.PROFILE) {
            // Como ProfileScreen aún no existe, creamos un placeholder aquí mismo.
            // Esta es una pantalla temporal muy simple.
            Profile(
                onProfileClick = {
                    navController.navigate(AppRoutes.GREETING)
                }
            )
        }
    }
}

