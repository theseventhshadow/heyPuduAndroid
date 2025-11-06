package com.heypudu.heypudu.navigation

import androidx.compose.runtime.Composable
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
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) AppRoutes.MAIN_GRAPH else OnboardingRoutes.GRAPH
    ) {
        onboardingGraph(navController)
        mainNavGraph(navController)
    }
}
