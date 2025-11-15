package com.heypudu.heypudu.features.mainscreen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.heypudu.heypudu.features.mainscreen.viewmodel.MainScreenViewModel
import com.heypudu.heypudu.ui.components.CreatePostBottomSheet
import com.heypudu.heypudu.ui.components.MainBottomPlayer
import com.heypudu.heypudu.ui.components.MainDrawer
import com.heypudu.heypudu.ui.components.MainTopBar
import com.heypudu.heypudu.ui.components.PostCard
import com.heypudu.heypudu.utils.LockScreenOrientation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(navController: NavHostController) {
    LockScreenOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val viewModel: MainScreenViewModel = viewModel()
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    val postsState by viewModel.posts.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh = {
        isRefreshing = true
        viewModel.repo.getPostsOnce { posts ->
            viewModel.setPosts(posts)
            // Añadir un pequeño delay para asegurar que la animación se vea
            coroutineScope.launch {
                delay(600)
                isRefreshing = false
            }
        }
    })

    // Obtener publicaciones reales desde Firestore
    LaunchedEffect(Unit) {
        viewModel.repo.getPosts { posts ->
            viewModel.setPosts(posts)
        }
    }

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
                            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                            val userId = auth.currentUser?.uid
                            android.util.Log.d("MainScreen", "Navegando a perfil desde Drawer. userId=$userId")
                            val currentRoute = navController.currentBackStackEntry?.destination?.route
                            val targetRoute = "profile_graph/profile_view?userId=$userId"
                            // Solo navega si no estamos ya en el perfil del usuario logueado
                            if (!userId.isNullOrEmpty() && currentRoute != targetRoute) {
                                navController.navigate(targetRoute) {
                                    popUpTo("main_graph") { inclusive = false }
                                    launchSingleTop = true
                                }
                            } else {
                                android.util.Log.e("MainScreen", "userId es nulo o ya estamos en perfil, no se navega")
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .pullRefresh(pullRefreshState)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                            .pullRefresh(pullRefreshState)
                    ) {
                        items(postsState) { post ->
                            PostCard(
                                post = post,
                                onNavigateToProfile = { authorId ->
                                    navController.navigate("profile_view?userId=$authorId")
                                }
                            )
                        }
                    }
                    PullRefreshIndicator(
                        refreshing = isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.TopCenter)
                    )
                }
                if (showSignOutDialog) {
                    AlertDialog(
                        onDismissRequest = { showSignOutDialog = false },
                        title = { Text(text = "¿Seguro de cerrar sesion?") },
                        confirmButton = {
                            TextButton(onClick = {
                                showSignOutDialog = false
                                viewModel.signOut {
                                    navController.navigate("greeting") {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
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
                            viewModel.createPost(title, message, audioUri) {
                                showCreatePostDialog = false
                            }
                        }
                    )
                }
            }
        }
    }
}