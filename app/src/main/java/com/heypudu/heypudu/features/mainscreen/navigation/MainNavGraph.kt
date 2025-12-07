package com.heypudu.heypudu.features.mainscreen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigation
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.heypudu.heypudu.navigation.AppRoutes
import com.heypudu.heypudu.features.mainscreen.ui.MainScreen
import com.heypudu.heypudu.features.news.ui.NewsScreen
import com.heypudu.heypudu.features.profile.ui.ProfileScreen

object MainRoutes {
    const val MAIN = "main_screen"
    const val NEWS = "news_screen"
    const val PROFILE = "profile_view"
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
            NewsScreen(navController)
        }
        composable(
            route = MainRoutes.PROFILE + "?userId={userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType; nullable = true })
        ) { navBackStackEntry ->
            val userId = navBackStackEntry.arguments?.getString("userId")
            ProfileScreen(
                userId = userId,
                navController = navController,
                onGoToEdit = { navController.navigate(MainRoutes.PROFILE + "?userId=$userId") }
            )
        }
    }

}