package com.heypudu.heypudu.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
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
        NavHost(
            navController = navController,
            startDestination = OnboardingRoutes.GRAPH
        ) {
            onboardingGraph(navController)
            mainNavGraph(navController)
        }
}

