package com.heypudu.heypudu.ui.components

import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
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
    val activityContext = (context as? Activity) ?: context
    val mediaPlayer = remember { MediaPlayer() }
    var isPrepared by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var positionMs by remember { mutableStateOf(0) }
    var durationMs by remember { mutableStateOf(0) }
    var likes by remember { mutableStateOf(initialLikes) }
    var audioError by remember { mutableStateOf<String?>(null) }

    // Depuración: logs de ciclo de vida
    fun logEvent(event: String) {
        Log.d("PostCard-MediaPlayer", "[$author] $event | isPrepared=$isPrepared | isPlaying=$isPlaying | positionMs=$positionMs | durationMs=$durationMs")
    }

    // Configurar fuente de audio cuando cambian audioRes/audioUrl
    LaunchedEffect(audioRes, audioUrl) {
        logEvent("LaunchedEffect: cambio de audioRes/audioUrl")
        mediaPlayer.reset()
        isPrepared = false
        isPlaying = false
        positionMs = 0
        durationMs = 0
        audioError = null
        try {
            if (audioRes != null) {
                logEvent("Inicializando MediaPlayer con recurso raw")
                val afd = context.resources.openRawResourceFd(audioRes)
                mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                mediaPlayer.prepare()
                isPrepared = true
                durationMs = mediaPlayer.duration
                logEvent("MediaPlayer preparado (raw)")
            } else if (!audioUrl.isNullOrBlank()) {
                logEvent("Inicializando MediaPlayer con contexto: ${activityContext::class.java.name} y url: $audioUrl")
                mediaPlayer.setDataSource(activityContext, Uri.parse(audioUrl))
                mediaPlayer.setOnPreparedListener {
                    isPrepared = true
                    durationMs = mediaPlayer.duration
                    logEvent("MediaPlayer preparado (url)")
                }
                mediaPlayer.setOnErrorListener { _, what, extra ->
                    audioError = "No se pudo reproducir el audio."
                    logEvent("Error en MediaPlayer: what=$what, extra=$extra")
                    false
                }
                mediaPlayer.prepareAsync()
            }
        } catch (e: Exception) {
            logEvent("Error al inicializar MediaPlayer: ${e.message}")
            audioError = "No se pudo reproducir el audio."
            isPrepared = false
        }
    }

    DisposableEffect(audioRes, audioUrl) {
        logEvent("DisposableEffect: MediaPlayer creado")
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
            positionMs = 0
            logEvent("MediaPlayer completó reproducción")
        }
        mediaPlayer.setOnPreparedListener {
            isPrepared = true
            durationMs = mediaPlayer.duration
            logEvent("MediaPlayer preparado (listener)")
        }
        mediaPlayer.setOnErrorListener { _, what, extra ->
            audioError = "No se pudo reproducir el audio."
            logEvent("Error en MediaPlayer: what=$what, extra=$extra")
            false
        }
        onDispose {
            logEvent("DisposableEffect: liberando MediaPlayer")
            mediaPlayer.reset()
            mediaPlayer.release()
        }
    }

    // Actualizar posición mientras se reproduce
    LaunchedEffect(isPlaying, isPrepared) {
        logEvent("LaunchedEffect: isPlaying=$isPlaying, isPrepared=$isPrepared")
        while (isPlaying && isPrepared) {
            positionMs = mediaPlayer.currentPosition
            durationMs = mediaPlayer.duration
            logEvent("Actualizando posición de reproducción")
            delay(250)
        }
    }

    fun togglePlayPause() {
        if (!isPrepared) {
            logEvent("togglePlayPause: MediaPlayer no preparado")
            return
        }
        // Si la posición está al final, reiniciar a 0 antes de reproducir
        if (positionMs >= durationMs - 500) {
            mediaPlayer.seekTo(0)
            positionMs = 0
            logEvent("togglePlayPause: reinicio a posición 0")
        }
        if (isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
            logEvent("togglePlayPause: pausa")
        } else {
            // Reinicializa el MediaPlayer si está en estado completado
            if (!mediaPlayer.isPlaying && positionMs == 0) {
                logEvent("togglePlayPause: reinicializando MediaPlayer antes de reproducir")
                mediaPlayer.seekTo(0)
            }
            mediaPlayer.start()
            isPlaying = true
            logEvent("togglePlayPause: reproducción")
        }
    }

    fun seekToFraction(fraction: Float) {
        if (!isPrepared) {
            logEvent("seekToFraction: MediaPlayer no preparado")
            return
        }
        val seekPos = (fraction.coerceIn(0f, 1f) * durationMs).toInt()
        mediaPlayer.seekTo(seekPos)
        positionMs = seekPos
        logEvent("seekToFraction: posición actualizada a $seekPos ms")
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
                if (audioError != null) {
                    Text(text = audioError!!, color = Color.Red, fontSize = 14.sp)
                } else {
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