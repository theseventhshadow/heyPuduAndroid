package com.heypudu.heypudu.features.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.heypudu.heypudu.features.onboarding.navigation.OnboardingRoutes
import com.heypudu.heypudu.features.splash.ui.SplashScreen
object SplashNavRoutes {
    const val SPLASH = "splash"
}

fun NavGraphBuilder.splashGraph(
    onNavigateToOnboarding: () -> Unit
) {
    composable(SplashNavRoutes.SPLASH) {
        SplashScreen(onTimeout = onNavigateToOnboarding)
    }
}
