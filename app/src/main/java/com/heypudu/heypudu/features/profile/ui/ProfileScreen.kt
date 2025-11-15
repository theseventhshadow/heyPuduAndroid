package com.heypudu.heypudu.features.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heypudu.heypudu.features.profile.viewmodel.ProfileViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.heypudu.heypudu.ui.components.MainTopBar
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.heypudu.heypudu.ui.components.MainDrawer
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String?,
    navController: androidx.navigation.NavHostController,
    onGoToEdit: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel()
    val photoUrl by viewModel.photoUrl.collectAsState()
    val username by viewModel.username.collectAsState()
    val email by viewModel.email.collectAsState()
    val postCount by viewModel.postCount.collectAsState()
    val error by viewModel.error.collectAsState()

    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadUser(userId)
        }
    }

    var showSignOutDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainDrawer(
                onDestinationClick = { destination ->
                    coroutineScope.launch { drawerState.close() }
                    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                    val currentUserId = auth.currentUser?.uid
                    val currentRoute = navController.currentBackStackEntry?.destination?.route
                    val targetRoute = "profile_graph/profile_view?userId=$currentUserId"
                    when (destination) {
                        "main_graph" -> {
                            navController.navigate("main_graph") {
                                launchSingleTop = true
                            }
                        }
                        "profile_graph" -> {
                            // Solo navega si no estamos ya en el perfil del usuario logueado
                            if (!currentUserId.isNullOrEmpty() && currentRoute != targetRoute) {
                                navController.navigate(targetRoute) {
                                    popUpTo("main_graph") { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        }
                        "logout" -> {
                            showSignOutDialog = true
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainTopBar(
                onMenuClick = { coroutineScope.launch { drawerState.open() } },
                onLogoClick = {},
                onMusicClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (error != null) {
                Text(
                    text = error ?: "Error desconocido",
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    AsyncImage(
                        model = photoUrl ?: "https://ui-avatars.com/api/?name=${username ?: "?"}&background=33E7B2&color=fff",
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(72.dp).clip(CircleShape),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = username ?: "Usuario", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = email ?: "Sin correo", fontSize = 16.sp, color = androidx.compose.ui.graphics.Color.Gray)
                        Text(text = "Posts: $postCount", fontSize = 15.sp, color = androidx.compose.ui.graphics.Color.DarkGray)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onGoToEdit) {
                    Text("Editar Perfil")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack) {
                    Text("Volver")
                }
            }
        }
        if (showSignOutDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showSignOutDialog = false },
                title = { Text(text = "¿Seguro de cerrar sesion?") },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        showSignOutDialog = false
                        // Cerrar sesión usando FirebaseAuth directamente
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                        navController.navigate("greeting") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }) {
                        Text("Si")
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { showSignOutDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        userId = null,
        navController = androidx.navigation.compose.rememberNavController(),
        onGoToEdit = {},
        onBack = {}
    )
}
