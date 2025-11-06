package com.heypudu.heypudu.features.mainscreen.ui
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
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
    // Estado para publicaciones
    val postsState = remember { mutableStateOf(listOf(
        Triple("Angelo Millán", "05/11/2025", "¡Hola! Esta es mi primera publicación."),
        Triple("María Pudu", "04/11/2025", "¡Bienvenidos a HeyPudú!"),
        Triple("Juanito", "03/11/2025", "¿Alguien quiere escuchar música?")
    )) }
    Surface(color = Color.White){
        Column {
            MainTopBar(
                onMenuClick = {
                    if (!drawerState.isOpen) {
                        coroutineScope.launch { drawerState.open() }
                    } else {
                        coroutineScope.launch { drawerState.close() }
                    }
                },
                onLogoClick = {
                    // Si ya está en MainScreen, recarga publicaciones
                    postsState.value = listOf(
                        Triple("Angelo Millán", "06/11/2025", "¡Publicaciones recargadas!"),
                        Triple("María Pudu", "06/11/2025", "¡Bienvenidos de nuevo a HeyPudú!"),
                        Triple("Juanito", "06/11/2025", "¡Recarga exitosa!")
                    )
                    // Si está en otra pantalla, navega a MainScreen
                    navController.navigate("main_graph") {
                        launchSingleTop = true
                    }
                },
                onMusicClick = { /* TODO: Navegar a reproductor de música */ }
            )
            MainDrawer(
                drawerState = drawerState,
                onDestinationClick = { route ->
                    // TODO: Navegar según la ruta recibida
                    coroutineScope.launch { drawerState.close() }
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
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
        }
    }

}