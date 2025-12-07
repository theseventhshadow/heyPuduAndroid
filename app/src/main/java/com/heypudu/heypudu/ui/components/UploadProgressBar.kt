package com.heypudu.heypudu.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.heypudu.heypudu.utils.UploadStateManager

@Composable
fun UploadProgressBar() {
    val uploadState by UploadStateManager.uploadState.collectAsState()
    var showCompletedMessage by remember { mutableStateOf(false) }

    LaunchedEffect(uploadState.isCompleted) {
        if (uploadState.isCompleted) {
            // Esperar un poco para que se vea la barra al 100%
            delay(500)
            showCompletedMessage = true
            delay(5000) // Mostrar por 5 segundos
            showCompletedMessage = false
            UploadStateManager.resetState()
        }
    }

    AnimatedVisibility(
        visible = uploadState.isUploading || showCompletedMessage,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFA76A6))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Mensaje de estado
                Text(
                    text = if (showCompletedMessage) "¡Publicado!" else uploadState.message,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Barra de progreso (solo si no es el mensaje final)
                if (!showCompletedMessage) {
                    LinearProgressIndicator(
                        progress = { uploadState.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f),
                    )
                }
            }
        }
    }
}

