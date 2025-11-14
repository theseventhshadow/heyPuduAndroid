package com.heypudu.heypudu.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.heypudu.heypudu.data.Post
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

@Composable
fun PostCard(
    post: Post,
    modifier: Modifier = Modifier,
    onNavigateToProfile: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val audioId = "${post.authorUsername}-${post.publishedAt}-${post.audioUrl}"
    val audioState = AudioPlayerController.observeState().collectAsState()
    var likes by remember { mutableStateOf(post.likes?.size ?: 0) }
    var playCount by remember { mutableStateOf(post.playCount ?: 0) }
    val coroutineScope = rememberCoroutineScope()
    val userId = com.heypudu.heypudu.data.UserRepository().getCurrentUserId() ?: ""
    var hasLiked by remember { mutableStateOf(post.likes?.contains(userId) == true) }

    fun togglePlayPause() {
        if (post.audioUrl.isNullOrBlank()) return
        if (audioState.value.audioId == audioId && audioState.value.isPlaying) {
            AudioPlayerController.pause()
        } else {
            AudioPlayerController.play(context, audioId, post.audioUrl) {
                // onCompletion: solo aquí se cuenta la reproducción
                post.documentId?.let { docId: String ->
                    coroutineScope.launch {
                        com.heypudu.heypudu.data.UserRepository().incrementPlayCount(docId)
                        playCount++ // Actualiza el contador local solo al finalizar
                    }
                }
            }
        }
    }

    fun seekToFraction(fraction: Float) {
        val durationMs = if (audioState.value.audioId == audioId) audioState.value.durationMs else 0
        val seekPos = (fraction.coerceIn(0f, 1f) * durationMs).toInt()
        AudioPlayerController.seekTo(seekPos)
    }

    fun formatMs(ms: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF33E7B2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Título
                Text(
                    text = post.title ?: "Sin título",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                // Mensaje
                Text(
                    text = post.content ?: "",
                    fontSize = 15.sp,
                    color = Color.DarkGray,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
                // Autor
                Text(
                    text = post.authorUsername ?: "Autor desconocido",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Imagen de perfil circular clickable
            AsyncImage(
                model = post.authorPhotoUrl ?: "https://ui-avatars.com/api/?name=${post.authorUsername ?: "?"}&background=33E7B2&color=fff",
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .clickable { post.authorId?.let { onNavigateToProfile(it) } },
                contentScale = ContentScale.Crop
            )
        }
        // Reproductor de audio
        if (!post.audioUrl.isNullOrBlank()) {
            val isPlaying = audioState.value.audioId == audioId && audioState.value.isPlaying
            val positionMs = if (audioState.value.audioId == audioId) audioState.value.positionMs else 0
            val durationMs = if (audioState.value.audioId == audioId) audioState.value.durationMs else 0
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { togglePlayPause() },
                    modifier = Modifier.size(56.dp), // Tamaño estándar para botón de acción
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFA76A6),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp), // Cuadrado con esquinas mínimas
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        tint = Color.Black,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = if (durationMs > 0) positionMs.toFloat() / durationMs else 0f,
                    onValueChange = { seekToFraction(it) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = formatMs(positionMs), fontSize = 13.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = formatMs(if (durationMs > 0) durationMs else 0), fontSize = 13.sp, color = Color.DarkGray)
            }
        }
        // Indicadores de reproducciones y likes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${playCount} hey!",
                fontSize = 15.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = likes.toString(), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Corazón",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )
                TextButton(
                    onClick = {
                        val newLiked = !hasLiked
                        hasLiked = newLiked
                        coroutineScope.launch {
                            post.documentId?.let { docId ->
                                com.heypudu.heypudu.features.mainscreen.viewmodel.MainScreenViewModel().toggleLikePost(
                                    docId, userId, newLiked
                                ) { success ->
                                    if (success) {
                                        likes = if (newLiked) likes + 1 else likes - 1
                                    }
                                }
                            }
                        }
                    },
                    content = {
                        Text("Pudús", fontSize = 15.sp)
                    }
                )
            }
        }
    }
}