package com.heypudu.heypudu.ui.components

import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

@Composable
fun MainDrawer(
    drawerState: DrawerState,
    onDestinationClick: (String) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { androidx.compose.material3.Text("Inicio") },
                    selected = false,
                    onClick = { onDestinationClick("main_graph") },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF33E7B2),
                        unselectedContainerColor = Color.White
                    )
                )
                NavigationDrawerItem(
                    label = { androidx.compose.material3.Text("Perfil") },
                    selected = false,
                    onClick = { onDestinationClick("profile_graph") },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF33E7B2),
                        unselectedContainerColor = Color.White
                    )
                )
                NavigationDrawerItem(
                    label = { androidx.compose.material3.Text("Cerrar sesión") },
                    selected = false,
                    onClick = { onDestinationClick("logout") },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFFA76A6),
                        unselectedContainerColor = Color.White
                    )
                )
            }
        },
        content = content
    )
}

