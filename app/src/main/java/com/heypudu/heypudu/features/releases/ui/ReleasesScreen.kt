package com.heypudu.heypudu.features.releases.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.heypudu.heypudu.ui.components.MainTopBar
import com.heypudu.heypudu.ui.components.AnimatedGradientBackground
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import MainDrawer
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleasesScreen(
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainDrawer(
                onDestinationClick = { destination ->
                    coroutineScope.launch { drawerState.close() }
                    val auth = FirebaseAuth.getInstance()
                    val currentUserId = auth.currentUser?.uid
                    when (destination) {
                        "main_graph" -> {
                            navController.navigate("main_graph") {
                                launchSingleTop = true
                            }
                        }
                        "profile_graph" -> {
                            navController.navigate("profile_view?userId=$currentUserId") {
                                launchSingleTop = true
                            }
                        }
                        "news_screen" -> {
                            navController.navigate("news_screen") {
                                launchSingleTop = true
                            }
                        }
                        "logout" -> {
                            // TODO: Implementar logout
                        }
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedGradientBackground {

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    MainTopBar(
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        onLogoClick = {
                            navController.navigate("main_graph") {
                                launchSingleTop = true
                            }
                        },
                        onMusicClick = {}
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "🎵 Lanzamientos",
                        fontSize = 28.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Descubre álbumes y podcasts de artistas en heyPudú",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Próximamente: Sección de lanzamientos públicos",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

