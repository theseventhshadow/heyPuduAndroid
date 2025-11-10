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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.heypudu.heypudu.features.mainscreen.viewmodel.MainScreenViewModel
import com.heypudu.heypudu.ui.components.CreatePostBottomSheet
import com.heypudu.heypudu.ui.components.MainBottomPlayer
import com.heypudu.heypudu.ui.components.MainDrawer
import com.heypudu.heypudu.ui.components.MainTopBar
import com.heypudu.heypudu.ui.components.PostCard
import com.heypudu.heypudu.utils.LockScreenOrientation
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
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

    // Obtener publicaciones reales desde Firestore
    LaunchedEffect(Unit) {
        viewModel.repo.getPosts { posts ->
            viewModel.setPosts(posts)
        }
    }

    fun refreshPosts() {
        isRefreshing = true
        viewModel.repo.getPosts { posts ->
            viewModel.setPosts(posts)
            isRefreshing = false
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
                ) {
                    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = { refreshPosts() }
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            if (postsState.isEmpty()) {
                                item {
                                    Text(
                                        text = "No hay publicaciones disponibles.",
                                        color = Color.Gray,
                                        modifier = Modifier.padding(32.dp)
                                    )
                                }
                            } else {
                                items(postsState) { post ->
                                    PostCard(
                                        author = post.authorUsername ?: "",
                                        date = post.publishedAt?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "",
                                        content = post.content ?: "",
                                        audioUrl = post.audioUrl
                                    )
                                }
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
                                viewModel.signOut()
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
                            val userId = viewModel.repo.getCurrentUserId() ?: ""
                            val post = com.heypudu.heypudu.data.Post(
                                authorId = userId,
                                authorUsername = authorUsername,
                                authorPhotoUrl = authorPhotoUrl,
                                publishedAt = publishedAt,
                                title = title,
                                content = message,
                                audioUrl = "",
                                likes = emptyList(),
                                comments = emptyList()
                            )
                            viewModel.createPost(post, audioUri) {
                                showCreatePostDialog = false
                            }
                        }
                    )
                }
            }
        }
    }
}