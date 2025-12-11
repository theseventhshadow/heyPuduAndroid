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
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
fun PostCard(
    post: Post,
    modifier: Modifier = Modifier,
    onNavigateToProfile: (String) -> Unit = {},
    onNavigateToAudioPlayer: ((Post) -> Unit)? = null,
    setCurrentPlayingPost: ((Post) -> Unit)? = null,
    viewModel: com.heypudu.heypudu.features.mainscreen.viewmodel.MainScreenViewModel? = null // Nuevo parámetro opcional
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
        if (post.audioUrl.isNullOrBlank()) {
            android.util.Log.d("PostCard-Audio", "URL de audio vacía o nula")
            return
        }
        val cacheDir = context.cacheDir
        // Usar solo documentId o un identificador seguro para el nombre del archivo
        val safeId = post.documentId ?: "audio_${post.authorUsername}_${post.publishedAt}"
        val audioFileName = "audio_${safeId}.m4a"
        val audioFile = File(cacheDir, audioFileName)
        val localAudioPath = if (audioFile.exists()) audioFile.absolutePath else null
        android.util.Log.d("PostCard-Audio", "Intentando reproducir: audioId=$audioId, localAudioPath=$localAudioPath, audioUrl=${post.audioUrl}")
        if (audioState.value.audioId == audioId && audioState.value.isPlaying) {
            android.util.Log.d("PostCard-Audio", "Pausando audio actual")
            AudioPlayerController.pause()
        } else {
            if (localAudioPath != null) {
                android.util.Log.d("PostCard-Audio", "Reproduciendo desde archivo local: $localAudioPath")
                AudioPlayerController.play(context, audioId, localAudioPath) {
                    post.documentId?.let { docId: String ->
                        coroutineScope.launch {
                            com.heypudu.heypudu.data.UserRepository().incrementPlayCount(docId)
                            playCount++
                        }
                    }
                }
            } else {
                android.util.Log.d("PostCard-Audio", "Descargando audio desde: ${post.audioUrl}")
                coroutineScope.launch {
                    try {
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            val url = URL(post.audioUrl)
                            url.openStream().use { input ->
                                FileOutputStream(audioFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                        }
                        android.util.Log.d("PostCard-Audio", "Descarga exitosa: ${audioFile.absolutePath}")
                        AudioPlayerController.play(context, audioId, audioFile.absolutePath) {
                            post.documentId?.let { docId: String ->
                                coroutineScope.launch {
                                    com.heypudu.heypudu.data.UserRepository().incrementPlayCount(docId)
                                    playCount++
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("PostCard-Audio", "Error al descargar o reproducir: ${e.message}", e)
                    }
                }
            }
        }
        if (setCurrentPlayingPost != null) {
            setCurrentPlayingPost(post)
        } else if (viewModel != null) {
            viewModel.setCurrentPlayingPost(post)
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

    fun formatPublishedDate(publishedAt: Long?): String {
        if (publishedAt == null) return "Fecha desconocida"
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return formatter.format(java.util.Date(publishedAt))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF33E7B2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                onNavigateToAudioPlayer?.invoke(post)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Título
                Text(
                    text = post.title ?: "Sin título",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
                // Fecha de publicación
                Text(
                    text = formatPublishedDate(post.publishedAt),
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
                // Mensaje
                Text(
                    text = post.content ?: "",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
                // Autor
                Text(
                    text = post.authorUsername ?: "Autor desconocido",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            // Imagen de perfil circular clickable
            AsyncImage(
                model = post.authorPhotoUrl ?: painterResource(id = com.heypudu.heypudu.R.drawable.ic_pudu_logo),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(44.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Button(
                    onClick = { togglePlayPause() },
                    modifier = Modifier.size(40.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFA76A6),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.Black)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Slider(
                    value = if (durationMs > 0) positionMs.toFloat() / durationMs else 0f,
                    onValueChange = { seekToFraction(it) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = formatMs(positionMs), fontSize = 10.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.width(1.dp))
                Text(text = formatMs(if (durationMs > 0) durationMs else 0), fontSize = 10.sp, color = Color.DarkGray)
            }
        }
        // Indicadores de reproducciones y likes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${playCount} heyplays!",
                fontSize = 11.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = likes.toString(), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Corazón",
                    tint = Color.Red,
                    modifier = Modifier.size(14.dp)
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
                        Text("pudús", fontSize = 11.sp)
                    }
                )
            }
        }
    }
}

@Composable
fun rememberProfileImagePainter(profileUrl: String?, authorId: String): Painter {
    val context = LocalContext.current
    var localPath by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(profileUrl) {
        if (!profileUrl.isNullOrBlank() && profileUrl.startsWith("http")) {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, "profile_$authorId.jpg")
            if (!file.exists()) {
                try {
                    val url = URL(profileUrl)
                    url.openStream().use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {
                    // Si falla, no actualiza localPath
                }
            }
            if (file.exists()) localPath = file.absolutePath
        }
    }
    return when {
        localPath != null -> {
            coil.compose.rememberAsyncImagePainter(model = File(localPath!!))
        }
        !profileUrl.isNullOrBlank() -> {
            coil.compose.rememberAsyncImagePainter(model = profileUrl)
        }
        else -> {
            painterResource(id = com.heypudu.heypudu.R.drawable.ic_pudu_logo)
        }
    }
}
