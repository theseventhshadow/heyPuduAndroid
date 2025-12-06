package com.heypudu.heypudu.features.mainscreen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigation
import androidx.navigation.compose.composable
import com.heypudu.heypudu.navigation.AppRoutes
import com.heypudu.heypudu.features.mainscreen.ui.MainScreen
import com.heypudu.heypudu.features.news.ui.NewsScreen

object MainRoutes {
    const val MAIN = "main_screen"
    const val NEWS = "news_screen"
}

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    navigation(
        startDestination = MainRoutes.MAIN,
        route = AppRoutes.MAIN_GRAPH
    ) {
        composable(MainRoutes.MAIN) {
            MainScreen(navController)
        }
        composable(MainRoutes.NEWS) {
            NewsScreen()
        }
    }

}