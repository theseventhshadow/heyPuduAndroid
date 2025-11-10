package com.heypudu.heypudu.ui.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.media.MediaRecorder
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import java.io.File
var permissionDenied by mutableStateOf(false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostBottomSheet(
    show: Boolean,
    onDismiss: () -> Unit,
    onPost: (title: String, message: String, audioUri: Uri?, authorUsername: String, authorPhotoUrl: String, publishedAt: Long, dateString: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    val audioPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) audioUri = uri
    }

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var authorUsername by remember { mutableStateOf("") }
    var authorPhotoUrl by remember { mutableStateOf("") }
    val publishedAt = System.currentTimeMillis()
    val dateString = remember(publishedAt) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(publishedAt))
    }

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

    var isRecording by remember { mutableStateOf(false) }
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    var recordedFilePath by remember { mutableStateOf("") }
    var recordingSeconds by remember { mutableStateOf(0) }

    fun startRecording() {
        val fileName = "audio_${System.currentTimeMillis()}.m4a"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
        recordedFilePath = file.absolutePath
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(recordedFilePath)
            prepare()
            start()
        }
        isRecording = true
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        audioUri = Uri.fromFile(File(recordedFilePath))
    }

    fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    var showSettingsButton by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            startRecording()
            permissionDenied = false
            showSettingsButton = false
        } else {
            permissionDenied = true
            // Si el usuario deniega el permiso, verificamos si es permanente
            val shouldShowRationale = androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
                (context as? android.app.Activity) ?: return@rememberLauncherForActivityResult,
                Manifest.permission.RECORD_AUDIO
            )
            showSettingsButton = !shouldShowRationale
        }
    }

    // Actualiza el contador de grabación mientras isRecording es true
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingSeconds = 0
            while (isRecording) {
                kotlinx.coroutines.delay(1000)
                recordingSeconds++
            }
        }
    }

    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            dragHandle = { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { HorizontalDivider(thickness = 3.dp, modifier = Modifier.width(40.dp).padding(vertical = 8.dp), color = Color(0xFF33E7B2)) } },
            containerColor = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Nueva publicación de audio", fontSize = 18.sp, color = Color(0xFFE91E63), modifier = Modifier.padding(bottom = 12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Descripción/Mensaje") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = { audioPickerLauncher.launch("audio/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF33E7B2))
                    ) {
                        Text("Elegir audio", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (!isRecording) {
                                if (hasAudioPermission()) {
                                    startRecording()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            } else {
                                stopRecording()
                            }
                        },
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                    ) {
                        Text(if (isRecording) "Detener grabación" else "Grabar audio", color = Color.White)
                    }
                }
                if (isRecording) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val min = recordingSeconds / 60
                    val sec = recordingSeconds % 60
                    Text("Grabando: %02d:%02d".format(min, sec), fontSize = 14.sp, color = Color(0xFFE91E63))
                }
                if (audioUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Audio seleccionado", fontSize = 12.sp, color = Color(0xFF33E7B2))
                }
                if (permissionDenied) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Permiso de grabación denegado. No se puede grabar audio.", fontSize = 12.sp, color = Color.Red)
                }
                if (showSettingsButton) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                        context.startActivity(intent)
                    }) {
                        Text("Abrir ajustes para habilitar el micrófono", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        onPost(title, message, audioUri, authorUsername, authorPhotoUrl, publishedAt, dateString)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text("Publicar", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
