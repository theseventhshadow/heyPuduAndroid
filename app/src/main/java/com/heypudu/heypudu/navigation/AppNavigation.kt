package com.heypudu.heypudu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.heypudu.heypudu.features.onboarding.navigation.OnboardingRoutes
import com.heypudu.heypudu.features.onboarding.navigation.onboardingGraph
import com.heypudu.heypudu.features.mainscreen.navigation.mainNavGraph
import com.heypudu.heypudu.features.profile.navigation.profileGraph
import com.heypudu.heypudu.features.releases.navigation.releasesGraph
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment


object AppRoutes {
    const val MAIN_GRAPH = "main_graph"
    const val PROFILE_GRAPH = "profile_graph"
    const val RELEASES_GRAPH = "releases_graph"
}

@Composable
fun AppNavigation() {
    android.util.Log.d("AppNavigation", "=== AppNavigation RENDERIZADO ===")

    val navController = rememberNavController()
    var authState by remember { mutableStateOf<AuthState>(AuthState.Loading) }
    var startDestination by remember { mutableStateOf<String?>(null) }

    android.util.Log.d("AppNavigation", "Inicializando AppNavigation. authState=$authState")

    // Determinar estado de autenticación una sola vez al montar el composable
    androidx.compose.runtime.LaunchedEffect(Unit) {
        android.util.Log.d("AppNavigation", "LaunchedEffect ejecutándose")
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val newAuthState = if (currentUser != null && currentUser.isEmailVerified) {
            android.util.Log.d("AppNavigation", "Usuario autenticado y email verificado: ${currentUser.uid}")
            AuthState.Authenticated
        } else if (currentUser != null) {
            android.util.Log.d("AppNavigation", "Usuario autenticado pero email no verificado: ${currentUser.uid}")
            AuthState.Unauthenticated
        } else {
            android.util.Log.d("AppNavigation", "Usuario no autenticado")
            AuthState.Unauthenticated
        }

        authState = newAuthState

        // Establecer startDestination basado en el estado
        startDestination = when (newAuthState) {
            AuthState.Authenticated -> {
                android.util.Log.d("AppNavigation", "Navegando a MAIN_GRAPH")
                AppRoutes.MAIN_GRAPH
            }
            else -> {
                android.util.Log.d("AppNavigation", "Navegando a OnboardingRoutes.GRAPH")
                OnboardingRoutes.GRAPH
            }
        }

        // Añadir listener para cambios futuros
        val listener = FirebaseAuth.AuthStateListener { authInstance ->
            val user = authInstance.currentUser
            val newState = if (user != null && user.isEmailVerified) {
                android.util.Log.d("AppNavigation", "AuthStateListener: Usuario autenticado y verificado: ${user.uid}")
                AuthState.Authenticated
            } else {
                android.util.Log.d("AppNavigation", "AuthStateListener: Usuario no autenticado")
                AuthState.Unauthenticated
            }
            authState = newState
        }
        auth.addAuthStateListener(listener)
    }

    // Si startDestination es null, mostrar loading
    if (startDestination == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    android.util.Log.d("AppNavigation", "Creando NavHost con startDestination=$startDestination")

    NavHost(
        navController = navController,
        startDestination = startDestination!!
    ) {
        android.util.Log.d("AppNavigation", "Registrando grafos...")
        onboardingGraph(navController)
        mainNavGraph(navController)
        profileGraph(navController)
        releasesGraph(navController)
        android.util.Log.d("AppNavigation", "Grafos registrados exitosamente")
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}
