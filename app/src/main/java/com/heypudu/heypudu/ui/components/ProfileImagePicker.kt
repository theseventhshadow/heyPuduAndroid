package com.heypudu.heypudu.ui.components

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.width
import com.heypudu.heypudu.R

@Composable
fun ProfileImagePicker(
    modifier: Modifier = Modifier,
    imageUri: MutableState<Uri?> = remember { mutableStateOf(null) }
) {
    val context = LocalContext.current
    val tempPhotoUri = remember { mutableStateOf<Uri?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success && tempPhotoUri.value != null) {
            imageUri.value = tempPhotoUri.value
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri.value = uri
        }
    }
    fun createImageFile(context: Context): Uri? {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(
            "profile_${System.currentTimeMillis()}",
            ".jpg",
            storageDir
        )
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )
    }

    Box(
        modifier = modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .border(2.dp, Color(0xFFE91E63), CircleShape)
            .clickable { showDialog.value = true },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri.value != null) {
            AsyncImage(
                model = imageUri.value,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(120.dp)
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_photo),
                contentDescription = "Añadir foto de perfil",
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
        }
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Selecciona una opción") },
            text = {
                Column {
                    Button(onClick = {
                        val uri = createImageFile(context)
                        if (uri != null) {
                            tempPhotoUri.value = uri
                            cameraLauncher.launch(uri)
                        }
                        showDialog.value = false
                    }, modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar foto")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        galleryLauncher.launch("image/*")
                        showDialog.value = false
                    }, modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar de la galería")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}
