package com.heypudu.heypudu.features.news.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.heypudu.heypudu.features.news.viewmodel.NewsViewModel
import com.heypudu.heypudu.network.NewsArticle
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import MainDrawer
import kotlinx.coroutines.launch
import com.heypudu.heypudu.ui.components.MainTopBar
import android.content.Intent
import androidx.core.net.toUri

@Composable
fun NewsScreen(
    navController: NavHostController,
    viewModel: NewsViewModel = viewModel()
) {
    val musicalEvents by viewModel.musicalEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var showSignOutDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainDrawer(
                onDestinationClick = { route ->
                    coroutineScope.launch { drawerState.close() }
                    when (route) {
                        "main_graph" -> {
                            navController.navigate("main_graph") {
                                launchSingleTop = true
                            }
                        }
                        "profile_graph" -> {
                            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                            val userId = auth.currentUser?.uid
                            navController.navigate("profile_view?userId=$userId") {
                                launchSingleTop = true
                            }
                        }
                        "news_screen" -> {
                            // Ya estamos en noticias
                        }
                        "logout" -> {
                            showSignOutDialog = true
                        }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                MainTopBar(
                    onMenuClick = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onLogoClick = {
                        navController.navigate("main_graph") {
                            launchSingleTop = true
                        }
                    },
                    onMusicClick = {
                        navController.navigate("releases_graph") {
                            launchSingleTop = true
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Error Message
                if (error != null) {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Loading
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // News List
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(musicalEvents) { article ->
                        NewsArticleCard(article)
                    }
                }
            }
        }
    }
}

@Composable
fun NewsArticleCard(article: NewsArticle) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF33E7B2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título
            Text(
                text = article.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Fuente
            Text(
                text = article.source.name,
                fontSize = 12.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Descripción
            article.description?.let {
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Fecha
            Text(
                text = article.publishedAt,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Botón de leer más
            Text(
                text = "Leer más →",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, article.url.toUri())
                        context.startActivity(intent)
                    }
            )
        }
    }
}

