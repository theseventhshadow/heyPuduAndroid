package com.heypudu.heypudu.ui.components

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun PostCard(
    author: String,
    date: String,
    content: String,
    modifier: Modifier = Modifier,
    imageRes: Int? = null,
    audioRes: Int? = null,        // recurso raw (opcional)
    audioUrl: String? = null,     // url (opcional)
    initialLikes: Int = 0,
    onLike: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var isPrepared by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var positionMs by remember { mutableStateOf(0) }
    var durationMs by remember { mutableStateOf(0) }
    var likes by remember { mutableStateOf(initialLikes) }

    // configurar fuente de audio cuando cambian audioRes/audioUrl
    LaunchedEffect(audioRes, audioUrl) {
        mediaPlayer.reset()
        isPrepared = false
        positionMs = 0
        durationMs = 0
        if (audioRes != null) {
            val afd = context.resources.openRawResourceFd(audioRes)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer.prepare()
            isPrepared = true
            durationMs = mediaPlayer.duration
        } else if (!audioUrl.isNullOrBlank()) {
            mediaPlayer.setDataSource(context, Uri.parse(audioUrl))
            mediaPlayer.setOnPreparedListener {
                isPrepared = true
                durationMs = mediaPlayer.duration
            }
            mediaPlayer.prepareAsync()
        }
    }

    DisposableEffect(Unit) {
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
            positionMs = 0
        }
        onDispose {
            mediaPlayer.release()
        }
    }

    // actualizar posición mientras se reproduce
    LaunchedEffect(isPlaying, isPrepared) {
        while (isPlaying && isPrepared) {
            positionMs = mediaPlayer.currentPosition
            durationMs = mediaPlayer.duration
            delay(250)
        }
    }

    fun togglePlayPause() {
        if (!isPrepared) return
        if (isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        } else {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    fun seekToFraction(fraction: Float) {
        if (!isPrepared) return
        val seekPos = (fraction.coerceIn(0f, 1f) * durationMs).toInt()
        mediaPlayer.seekTo(seekPos)
        positionMs = seekPos
    }

    fun formatMs(ms: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF33E7B2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (imageRes != null) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Imagen de publicación",
                        modifier = Modifier.size(48.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column {
                    Text(
                        text = author,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                fontSize = 15.sp,
                color = Color.Black,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            if (audioRes != null || !audioUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { togglePlayPause() }) {
                        Text(if (isPlaying) "⏸" else "▶")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val fraction = if (durationMs > 0) positionMs.toFloat() / durationMs else 0f
                        Slider(
                            value = fraction,
                            onValueChange = { seekToFraction(it) }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = formatMs(positionMs), fontSize = 12.sp, color = Color.DarkGray)
                            Text(text = formatMs(if (durationMs > 0) durationMs else 0), fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = likes.toString(), fontWeight = FontWeight.Bold)
                        TextButton(onClick = {
                            likes++
                            onLike?.invoke(likes)
                        }) {
                            Text("Like")
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = likes.toString(), fontWeight = FontWeight.Bold)
                        TextButton(onClick = {
                            likes++
                            onLike?.invoke(likes)
                        }) {
                            Text("Like")
                        }
                    }
                }
            }
        }
    }
}