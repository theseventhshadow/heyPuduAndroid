package com.heypudu.heypudu.features.mainscreen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.heypudu.heypudu.data.UserRepository
import com.heypudu.heypudu.ui.components.CreatePostBottomSheet
import com.heypudu.heypudu.ui.components.MainBottomPlayer
import com.heypudu.heypudu.ui.components.MainDrawer
import com.heypudu.heypudu.ui.components.MainTopBar
import com.heypudu.heypudu.ui.components.PostCard
import com.heypudu.heypudu.utils.LockScreenOrientation
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    LockScreenOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val postsState = remember { mutableStateOf(listOf(
        Triple("Angelo Millán", "05/11/2025", "¡Hola! Esta es mi primera publicación."),
        Triple("María Pudu", "04/11/2025", "¡Bienvenidos a HeyPudú!"),
        Triple("Juanito", "03/11/2025", "¿Alguien quiere escuchar música?")
    )) }
    val repo = UserRepository()
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    Surface(color = Color.White) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                MainDrawer(
                    onDestinationClick = { route ->
                        coroutineScope.launch { drawerState.close() }
                        if (route == "logout") {
                            showSignOutDialog = true
                        } else if (route == "main_graph") {
                            navController.navigate("main_graph") {
                                launchSingleTop = true
                            }
                        } else if (route == "profile_graph") {
                            navController.navigate("profile_graph") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            },
        ) {
            Scaffold(
                topBar = {
                    MainTopBar(
                        onMenuClick = {
                            if (!drawerState.isOpen) {
                                coroutineScope.launch { drawerState.open() }
                            } else {
                                coroutineScope.launch { drawerState.close() }
                            }
                        },
                        onLogoClick = {
                            postsState.value = listOf(
                                Triple("Angelo Millán", "06/11/2025", "¡Publicaciones recargadas!"),
                                Triple("María Pudu", "06/11/2025", "¡Bienvenidos de nuevo a HeyPudú!"),
                                Triple("Juanito", "06/11/2025", "¡Recarga exitosa!")
                            )
                            navController.navigate("main_graph") {
                                launchSingleTop = true
                            }
                        },
                        onMusicClick = { /* TODO: Navegar a reproductor de música */ }
                    )
                },
                bottomBar = {
                    MainBottomPlayer(
                        // Puedes pasar aquí el estado del reproductor
                        onCreatePost = { showCreatePostDialog = true }
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(postsState.value) { post ->
                            PostCard(
                                author = post.first,
                                date = post.second,
                                content = post.third
                            )
                        }
                    }
                }

            }
            if (showSignOutDialog) {
                AlertDialog(
                    onDismissRequest = { showSignOutDialog = false },
                    title = { Text(text = "¿Seguro de cerrar sesion?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showSignOutDialog = false
                            repo.signOut()
                            navController.navigate("greeting") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }) {
                            Text("Si")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSignOutDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }
            if (showCreatePostDialog) {
                CreatePostBottomSheet(
                    show = showCreatePostDialog,
                    onDismiss = { showCreatePostDialog = false },
                    onPost = { title, message, audioUri, authorUsername, authorPhotoUrl, publishedAt, dateString ->
                        val userId = repo.getCurrentUserId() ?: ""
                        val post = com.heypudu.heypudu.data.Post(
                            authorId = userId,
                            authorUsername = authorUsername,
                            authorPhotoUrl = authorPhotoUrl,
                            publishedAt = publishedAt,
                            message = message,
                            audioUrl = audioUri?.toString() ?: "",
                            likes = emptyList(),
                            comments = emptyList()
                        )
                        coroutineScope.launch {
                            repo.savePost(post)
                        }
                    }
                )
            }
        }
    }

}