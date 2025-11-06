package com.heypudu.heypudu.features.mainscreen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import androidx.navigation.compose.composable
import com.heypudu.heypudu.navigation.AppRoutes
import com.heypudu.heypudu.features.mainscreen.ui.MainScreen

object MainRoutes {
    const val MAIN = "main_screen"
}

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    navigation(
        startDestination = MainRoutes.MAIN,
        route = AppRoutes.MAIN_GRAPH
    ) {
        composable(MainRoutes.MAIN) {
            MainScreen(navController)
        }
    }

}