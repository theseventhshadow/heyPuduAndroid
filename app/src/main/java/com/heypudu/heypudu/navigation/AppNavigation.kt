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


object AppRoutes {
    const val MAIN_GRAPH = "main_graph"
    const val PROFILE_GRAPH = "profile_graph"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }
    var isEmailVerified by remember { mutableStateOf(false) }
    // Observa los cambios de autenticación y fuerza recomposición
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            isLoggedIn = user != null
            isEmailVerified = user?.isEmailVerified ?: false
        }
        auth.addAuthStateListener(listener)
    }
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn && isEmailVerified) AppRoutes.MAIN_GRAPH else OnboardingRoutes.GRAPH
    ) {
        onboardingGraph(navController)
        mainNavGraph(navController)
    }
}
