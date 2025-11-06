package com.heypudu.heypudu.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainBottomPlayer(
    songTitle: String = "Canción actual",
    artist: String = "Artista",
    isPlaying: Boolean = false,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrev: () -> Unit = {},
    onCreatePost: () -> Unit = {}
) {
    BottomAppBar(
        containerColor = Color(0xFF33E7B2),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini reproductor
            IconButton(onClick = onPrev) {
                Icon(Icons.Filled.SkipPrevious, contentDescription = "Anterior")
            }
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir"
                )
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Filled.SkipNext, contentDescription = "Siguiente")
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = songTitle,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
                Text(
                    text = artist,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.DarkGray
                )
            }
            // Botón para crear publicación
            IconButton(onClick = onCreatePost) {
                Icon(Icons.Filled.Mic, contentDescription = "Crear publicación")
            }
        }
    }
}