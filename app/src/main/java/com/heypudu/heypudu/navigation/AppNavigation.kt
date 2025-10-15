package com.heypudu.heypudu.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.heypudu.heypudu.features.onboarding.navigation.OnboardingRoutes
import com.heypudu.heypudu.features.onboarding.navigation.onboardingGraph
import com.heypudu.heypudu.features.profile.ProfileRoutes
import com.heypudu.heypudu.features.profile.profileGraph


object AppRoutes {
    const val PROFILE_GRAPH = ProfileRoutes.GRAPH
}

@Composable
fun AppNavigation() {

        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = OnboardingRoutes.GRAPH
        ) {
            onboardingGraph(navController)
            profileGraph(navController)
        }
}

