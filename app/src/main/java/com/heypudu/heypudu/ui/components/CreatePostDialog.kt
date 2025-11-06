package com.heypudu.heypudu.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heypudu.heypudu.data.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreatePostDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onPost: (title: String, message: String, audioUri: Uri?, authorUsername: String, authorPhotoUrl: String, publishedAt: Long, dateString: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    val audioPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) audioUri = uri
    }

    // Obtener datos del usuario actual
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var authorUsername by remember { mutableStateOf("") }
    var authorPhotoUrl by remember { mutableStateOf("") }
    val publishedAt = System.currentTimeMillis()
    val dateString = remember(publishedAt) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(publishedAt))
    }

    // Consultar Firestore solo si hay usuario
    LaunchedEffect(user?.uid) {
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    authorUsername = doc.getString("username") ?: ""
                    authorPhotoUrl = doc.getString("photoUrl") ?: ""
                }
        }
    }

    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Nueva publicación de audio") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Descripción/Mensaje") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(onClick = { audioPickerLauncher.launch("audio/*") }) {
                            Text("Elegir audio")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { isRecording = !isRecording }, enabled = false) {
                            Text(if (isRecording) "Detener grabación" else "Grabar audio")
                        }
                    }
                    if (audioUri != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Audio seleccionado: ${audioUri.toString()}", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onPost(title, message, audioUri, authorUsername, authorPhotoUrl, publishedAt, dateString)
                    onDismiss()
                }) {
                    Text("Publicar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview
@Composable
fun CreatePostDialogPreview() {
    CreatePostDialog(
        show = true,
        onDismiss = {},
        onPost = { _, _, _, _, _, _, _ -> }
    )
}
