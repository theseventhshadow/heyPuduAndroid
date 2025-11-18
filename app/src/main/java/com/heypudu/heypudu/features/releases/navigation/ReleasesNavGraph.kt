package com.heypudu.heypudu.features.releases.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.heypudu.heypudu.features.releases.ui.ReleasesScreen

object ReleasesRoutes {
    const val GRAPH = "releases_graph"
    const val RELEASES = "releases"
}

fun NavGraphBuilder.releasesGraph(navController: NavHostController) {
    navigation(
        startDestination = ReleasesRoutes.RELEASES,
        route = ReleasesRoutes.GRAPH
    ) {
        composable(route = ReleasesRoutes.RELEASES) {
            ReleasesScreen(navController = navController)
        }
    }
}

