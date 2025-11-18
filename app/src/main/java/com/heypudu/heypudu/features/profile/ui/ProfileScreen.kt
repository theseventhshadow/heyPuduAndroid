package com.heypudu.heypudu.features.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import MainDrawer
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.heypudu.heypudu.ui.components.ProfileImage
import com.heypudu.heypudu.ui.components.CreateReleaseBottomSheet



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
            // Corregido: solo una función loadUser
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
                onMusicClick = {
                    navController.navigate("releases_graph") {
                        launchSingleTop = true
                    }
                }
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
                    val context = LocalContext.current
                    ProfileImage(
                        context = context,
                        userId = userId,
                        photoUrl = photoUrl,
                        size = 72.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = username ?: "Cargando nombre de usuario...", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = email ?: "Cargando correo...", fontSize = 16.sp, color = androidx.compose.ui.graphics.Color.Gray)
                        Text(text = "$postCount pudúPosts", fontSize = 15.sp, color = androidx.compose.ui.graphics.Color.DarkGray)
                        val followerCount by viewModel.followerCount.collectAsState()
                        Text(text = "$followerCount seguidores", fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray)
                    }
                    val isFollowing by viewModel.isFollowing.collectAsState()
                    val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null && userId != currentUserId) {
                        Button(
                            onClick = { viewModel.toggleFollowUser(userId) },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(if (isFollowing) "Dejar de seguir" else "Seguir")
                        }
                    } else if (userId == currentUserId) {
                        Button(
                            onClick = { onGoToEdit() },
                            modifier = Modifier.height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color(0xFFE91E63)
                            )
                        ) {
                            Text("Editar Perfil")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Tabs para publicaciones, pudús y lanzamientos
                var selectedTabIndex by remember { mutableStateOf(0) }
                val tabTitles = listOf("Mis publicaciones", "Pudús", "Lanzamientos")
                androidx.compose.material3.PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                    tabTitles.forEachIndexed { index, title ->
                        androidx.compose.material3.Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                when (selectedTabIndex) {
                    0 -> {
                        // Publicaciones hechas por el usuario
                        val userPosts by viewModel.userPosts.collectAsState()
                        android.util.Log.d("ProfileScreen", "userPosts size: ${userPosts.size}")
                        if (userPosts.isEmpty()) {
                            Text("No tienes publicaciones", color = androidx.compose.ui.graphics.Color.Gray)
                        } else {
                            LazyColumn {
                                items(userPosts) { post ->
                                    com.heypudu.heypudu.ui.components.PostCard(
                                        post = post,
                                        onNavigateToProfile = { authorId ->
                                            navController.navigate("profile_graph/profile_view?userId=$authorId")
                                        }
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // Publicaciones que el usuario ha dado pudú
                        val likedPosts by viewModel.likedPosts.collectAsState()
                        android.util.Log.d("ProfileScreen", "likedPosts size: ${likedPosts.size}")
                        if (likedPosts.isEmpty()) {
                            Text("No has dado Pudús a ninguna publicación", color = androidx.compose.ui.graphics.Color.Gray)
                        } else {
                            LazyColumn {
                                items(likedPosts) { post ->
                                    com.heypudu.heypudu.ui.components.PostCard(
                                        post = post,
                                        onNavigateToProfile = { authorId ->
                                            navController.navigate("profile_graph/profile_view?userId=$authorId")
                                        }
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // Lanzamientos (álbumes y podcasts) del usuario
                        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                        val isCurrentUser = userId == currentUserId

                        var showCreateReleaseDialog by remember { mutableStateOf(false) }

                        Column {
                            if (isCurrentUser) {
                                Button(
                                    onClick = { showCreateReleaseDialog = true },
                                    modifier = Modifier
                                        .align(androidx.compose.ui.Alignment.CenterHorizontally)
                                        .padding(bottom = 16.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = androidx.compose.ui.graphics.Color(0xFFE91E63)
                                    )
                                ) {
                                    Text("+ Crear Lanzamiento")
                                }
                            }

                            val userAlbums by viewModel.userAlbums.collectAsState()
                            val userPodcasts by viewModel.userPodcasts.collectAsState()

                            if (userAlbums.isEmpty() && userPodcasts.isEmpty()) {
                                Text(
                                    "No hay lanzamientos aún",
                                    color = androidx.compose.ui.graphics.Color.Gray
                                )
                            } else {
                                LazyColumn {
                                    items(userAlbums.size) { index ->
                                        val album = userAlbums[index]
                                        androidx.compose.material3.Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "📀 ${album.title}",
                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = album.description,
                                                        fontSize = 12.sp,
                                                        color = androidx.compose.ui.graphics.Color.Gray
                                                    )
                                                    Text(
                                                        text = "Género: ${album.genre}",
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    items(userPodcasts.size) { index ->
                                        val podcast = userPodcasts[index]
                                        androidx.compose.material3.Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "🎙️ ${podcast.title}",
                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = podcast.description,
                                                        fontSize = 12.sp,
                                                        color = androidx.compose.ui.graphics.Color.Gray
                                                    )
                                                    Text(
                                                        text = "Categoría: ${podcast.category}",
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (showCreateReleaseDialog) {
                            CreateReleaseBottomSheet(
                                onDismiss = { showCreateReleaseDialog = false },
                                onCreateAlbum = { album ->
                                    viewModel.createAlbum(album)
                                    showCreateReleaseDialog = false
                                },
                                onCreatePodcast = { podcast ->
                                    viewModel.createPodcast(podcast)
                                    showCreateReleaseDialog = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
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

//@Preview(showBackground = true)
