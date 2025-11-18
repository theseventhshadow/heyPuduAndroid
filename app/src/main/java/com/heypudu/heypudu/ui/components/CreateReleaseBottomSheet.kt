package com.heypudu.heypudu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heypudu.heypudu.data.models.Album
import com.heypudu.heypudu.data.models.Podcast
import com.heypudu.heypudu.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import android.net.Uri
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReleaseBottomSheet(
    onDismiss: () -> Unit,
    onCreateAlbum: (Album) -> Unit,
    onCreatePodcast: (Podcast) -> Unit
) {
    var releaseType by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("es") }
    var frequency by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedCoverUri by remember { mutableStateOf<Uri?>(null) }
    var coverUrl by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val repo = UserRepository()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = Modifier.fillMaxHeight(0.95f)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Crear Lanzamiento",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar",
                            tint = Color.Black
                        )
                    }
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                if (releaseType == null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "¿Qué deseas crear?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Button(
                            onClick = { releaseType = "album" },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE91E63)
                            )
                        ) {
                            Text("📀 Álbum Musical")
                        }
                        Button(
                            onClick = { releaseType = "podcast" },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9C27B0)
                            )
                        ) {
                            Text("🎙️ Podcast")
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = { releaseType = null },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text("← Volver")
                        }

                        Text(
                            text = if (releaseType == "album") "Crear Álbum" else "Crear Podcast",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 100.dp),
                            maxLines = 3
                        )

                        ReleaseCoverPicker(
                            onCoverSelected = { uri ->
                                selectedCoverUri = uri
                                Log.d("CreateRelease", "Portada seleccionada: $uri")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (releaseType == "album") {
                            OutlinedTextField(
                                value = genre,
                                onValueChange = { genre = it },
                                label = { Text("Género (Rock, Pop, Jazz, etc)") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1
                            )
                        }

                        if (releaseType == "podcast") {
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text("Categoría (Educativo, Entretenimiento, etc)") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1
                            )

                            OutlinedTextField(
                                value = frequency,
                                onValueChange = { frequency = it },
                                label = { Text("Frecuencia (Semanal, Mensual, etc)") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Idioma:")
                                Row {
                                    listOf("es" to "Español", "en" to "Inglés").forEach { (code, lang) ->
                                        FilterChip(
                                            selected = language == code,
                                            onClick = { language = code },
                                            label = { Text(lang) },
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Button(
                            onClick = {
                                if (title.isBlank()) {
                                    errorMessage = "El título es requerido"
                                    return@Button
                                }

                                isLoading = true
                                errorMessage = ""

                                coroutineScope.launch {
                                    try {
                                        if (selectedCoverUri != null) {
                                            val releaseId = System.currentTimeMillis().toString()
                                            val type = if (releaseType == "album") "albums" else "podcasts"
                                            coverUrl = repo.uploadReleaseCover(selectedCoverUri!!, releaseId, type)
                                            Log.d("CreateRelease", "Portada subida: $coverUrl")
                                        }

                                        if (releaseType == "album") {
                                            val album = Album(
                                                albumId = System.currentTimeMillis().toString(),
                                                artistId = currentUser?.uid ?: "",
                                                artistUsername = currentUser?.displayName ?: "Artista",
                                                artistPhotoUrl = currentUser?.photoUrl?.toString() ?: "",
                                                title = title,
                                                description = description,
                                                coverUrl = coverUrl,
                                                genre = genre,
                                                releaseDate = System.currentTimeMillis(),
                                                totalTracks = 0,
                                                createdAt = System.currentTimeMillis(),
                                                updatedAt = System.currentTimeMillis(),
                                                isPublished = true
                                            )
                                            onCreateAlbum(album)
                                            Log.d("CreateRelease", "Álbum creado: ${album.title}")
                                        } else {
                                            val podcast = Podcast(
                                                podcastId = System.currentTimeMillis().toString(),
                                                creatorId = currentUser?.uid ?: "",
                                                creatorUsername = currentUser?.displayName ?: "Podcaster",
                                                creatorPhotoUrl = currentUser?.photoUrl?.toString() ?: "",
                                                title = title,
                                                description = description,
                                                coverUrl = coverUrl,
                                                category = category,
                                                language = language,
                                                frequency = frequency,
                                                releaseDate = System.currentTimeMillis(),
                                                totalSeasons = 1,
                                                totalEpisodes = 0,
                                                createdAt = System.currentTimeMillis(),
                                                updatedAt = System.currentTimeMillis(),
                                                isPublished = true
                                            )
                                            onCreatePodcast(podcast)
                                            Log.d("CreateRelease", "Podcast creado: ${podcast.title}")
                                        }

                                        isLoading = false
                                        onDismiss()
                                    } catch (e: Exception) {
                                        errorMessage = "Error al crear lanzamiento: ${e.message}"
                                        isLoading = false
                                        Log.e("CreateRelease", "Error: ${e.message}")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = !isLoading && title.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE91E63)
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text("Crear ${if (releaseType == "album") "Álbum" else "Podcast"}")
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}