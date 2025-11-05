package com.heypudu.heypudu.features.onboarding.ui

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import android.net.Uri
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heypudu.heypudu.R
import com.heypudu.heypudu.features.onboarding.viewmodel.CreateProfileViewModel
import com.heypudu.heypudu.features.onboarding.viewmodel.NavigationEvent
import com.heypudu.heypudu.ui.theme.HeyPudúTheme
import com.heypudu.heypudu.utils.LockScreenOrientation
import com.heypudu.heypudu.ui.components.AnimatedGradientBackground
import com.heypudu.heypudu.ui.components.ProfileImagePicker

/*
 --- PANTALLA DE CREACIÓN DE PERFIL ---
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    // Renombrado para reflejar la nueva acción
    onNavigateToVerification: () -> Unit,
    viewModel: CreateProfileViewModel = viewModel()
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val uiState = viewModel.uiState

    // Escucha los eventos de navegación correctamente
    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToEmailVerification -> {
                    onNavigateToVerification()
                }
            }
        }
    }

    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val imageError = remember { mutableStateOf(false) }

    AnimatedGradientBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Comienza esta nueva experiencia",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 32.dp, bottom = 24.dp),
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(2.dp, Color(0xFFE91E63), CircleShape)
                        .clickable { /* TODO: Lógica para abrir la galería */ },
                    contentAlignment = Alignment.Center
                ) {
                    // Reemplazo el ícono por el selector de imagen
                    ProfileImagePicker(imageUri = imageUri)
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    label = { Text("Nombre de usuario (max. 20 caracteres)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    isError = uiState.isUsernameError,
                    supportingText = {
                        if (uiState.isUsernameError) {
                            Text(
                                "El nombre de usuario no puede estar vacío.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Ingresa tu contraseña (6 a 12 caracteres.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = uiState.isPasswordError,
                    supportingText = {
                        if (uiState.isPasswordError) {
                            Text(
                                text = "La contraseña debe tener entre 6 y 12 caracteres.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Ingresa tu correo electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    isError = uiState.isEmailError,
                    supportingText = {
                        if (uiState.isEmailError) {
                            Text(
                                "El correo electrónico no es válido.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (imageUri.value == null) {
                            imageError.value = true
                        } else {
                            imageError.value = false
                            viewModel.onSaveProfileClick(imageUri.value)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63)
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Guardar y Continuar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (imageError.value) {
                    Text(
                        text = "Debes seleccionar una foto de perfil.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Mostrar diálogo de error si corresponde
                if (uiState.showDialog && uiState.dialogMessage.isNotBlank()) {
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissDialog() },
                        confirmButton = {
                            Button(onClick = { viewModel.dismissDialog() }) {
                                Text("Aceptar")
                            }
                        },
                        title = { Text("Error") },
                        text = { Text(uiState.dialogMessage) }
                    )
                }
            }
        }
        }
    }


/*
 --- VISTA PREVIA PARA DISEÑAR EN AISLADO ---
*/
@Preview(showBackground = true)
@Composable
private fun CreateProfileScreenPreview() {
    HeyPudúTheme {
        CreateProfileScreen(onNavigateToVerification = {})
    }
}
