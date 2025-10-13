package com.heypudu.heypudu.features.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.heypudu.heypudu.features.profile.ui.EditProfileScreen
import com.heypudu.heypudu.features.profile.ui.ProfileScreen

// Rutas específicas para el perfil
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
        composable(route = ProfileRoutes.VIEW) {
            ProfileScreen(
                onGoToEdit = { navController.navigate(ProfileRoutes.EDIT) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = ProfileRoutes.EDIT) {
            EditProfileScreen( // <- Una futura pantalla de edición
                onSave = { navController.popBackStack() }
            )
        }
    }
}

