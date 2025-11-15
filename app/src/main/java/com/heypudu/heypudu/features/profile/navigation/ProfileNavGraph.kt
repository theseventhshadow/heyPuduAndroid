package com.heypudu.heypudu.features.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.heypudu.heypudu.features.profile.ui.EditProfileScreen
import com.heypudu.heypudu.features.profile.ui.ProfileScreen

object ProfileRoutes {
    const val GRAPH = "profile_graph"
    const val VIEW = "profile_view"
    const val EDIT = "profile_edit"
}

fun NavGraphBuilder.profileGraph(navController: NavHostController) {
    navigation(
        startDestination = ProfileRoutes.VIEW,
        route = ProfileRoutes.GRAPH
    ) {
        composable(
            route = ProfileRoutes.VIEW + "?userId={userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType; nullable = true })
        ) { navBackStackEntry ->
            val userId = navBackStackEntry.arguments?.getString("userId")
            ProfileScreen(
                userId = userId,
                navController = navController,
                onGoToEdit = { navController.navigate(ProfileRoutes.EDIT) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
