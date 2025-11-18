package com.heypudu.heypudu.features.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import android.net.Uri
import com.heypudu.heypudu.data.UserRepository
import com.heypudu.heypudu.features.profile.viewmodel.ProfileViewModel
import com.heypudu.heypudu.ui.components.ProfileImage
import com.heypudu.heypudu.ui.components.ProfileImagePicker
import com.heypudu.heypudu.ui.components.AnimatedGradientBackground
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: String,
    navController: NavHostController
) {
    val viewModel: ProfileViewModel = viewModel()
    val repo = UserRepository()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Estados para los campos
    var username by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val currentUsername by viewModel.username.collectAsState()
    val currentPhotoUrl by viewModel.photoUrl.collectAsState()

    // Cargar datos actuales
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
        username = currentUsername ?: ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TopBar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Editar Perfil",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Foto de perfil actual
                if (!currentPhotoUrl.isNullOrEmpty()) {
                    Text(
                        text = "Foto Actual",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.align(Alignment.Start).padding(start = 16.dp)
                    )
                    ProfileImage(
                        context = context,
                        userId = userId,
                        photoUrl = currentPhotoUrl ?: "",
                        size = 120.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                // Selector de foto
                Text(
                    text = "Nueva Foto",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.align(Alignment.Start).padding(start = 16.dp)
                )
                val imageUri = remember { mutableStateOf(selectedImageUri) }
                ProfileImagePicker(
                    imageUri = imageUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 12.dp)
                )
                LaunchedEffect(imageUri.value) {
                    selectedImageUri = imageUri.value
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Campo de nombre de usuario
                Text(
                    text = "Nombre de Usuario",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.align(Alignment.Start).padding(start = 16.dp)
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFE91E63),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedLabelColor = Color(0xFFE91E63),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mensajes de error o éxito
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFF6B6B),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (successMessage.isNotEmpty()) {
                    Text(
                        text = successMessage,
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.3f)
                        )
                    ) {
                        Text("Cancelar", color = Color.White)
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = ""
                                successMessage = ""

                                try {
                                    // Actualizar nombre de usuario
                                    if (username.isNotBlank() && username != currentUsername) {
                                        repo.updateUserProfileField(userId, "username", username)
                                        Log.d(
                                            "EditProfile",
                                            "Nombre de usuario actualizado: $username"
                                        )
                                    }

                                    // Subir foto si fue seleccionada
                                    if (selectedImageUri != null) {
                                        val photoUrl =
                                            repo.uploadProfilePhoto(selectedImageUri!!, userId)
                                        repo.updateUserProfileField(userId, "photoUrl", photoUrl)
                                        Log.d("EditProfile", "Foto actualizada: $photoUrl")
                                    }

                                    successMessage = "Perfil actualizado correctamente"
                                    // Recargar datos
                                    viewModel.loadUser(userId)

                                    // Volver después de 1.5 segundos
                                    kotlinx.coroutines.delay(1500)
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    errorMessage = "Error: ${e.message}"
                                    Log.e("EditProfile", "Error al guardar: ${e.message}")
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        enabled = !isLoading && (username.isNotBlank() || selectedImageUri != null),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE91E63)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

