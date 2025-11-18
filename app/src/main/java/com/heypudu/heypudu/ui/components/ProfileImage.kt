package com.heypudu.heypudu.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileImage(
    context: Context,
    userId: String?,
    photoUrl: String?,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val localFile = remember { mutableStateOf<File?>(null) }
    val defaultUrl = "https://ui-avatars.com/api/?name=${userId ?: "?"}&background=33E7B2&color=fff"
    val imageUrl = photoUrl ?: defaultUrl

    LaunchedEffect(imageUrl) {
        if (imageUrl.startsWith("http") && userId != null) {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, "profile_$userId.jpg")
            if (!file.exists()) {
                try {
                    withContext(Dispatchers.IO) {
                        URL(imageUrl).openStream().use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ProfileImage", "Error descargando imagen: ${e.message}")
                }
            }
            if (file.exists()) localFile.value = file
        }
    }

    val painter = if (localFile.value != null) {
        rememberAsyncImagePainter(localFile.value)
    } else {
        rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build()
        )
    }

    Image(
        painter = painter,
        contentDescription = "Foto de perfil",
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colorFilter = null
    )
}

