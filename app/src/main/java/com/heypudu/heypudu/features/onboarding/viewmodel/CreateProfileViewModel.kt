package com.heypudu.heypudu.features.onboarding.viewmodel

import android.net.Uri
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.heypudu.heypudu.data.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class CreateProfileUiState(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val isUsernameError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isEmailError: Boolean = false,
    val isLoading: Boolean = false,
    val showDialog: Boolean = false,
    val dialogMessage: String = ""
)

sealed class NavigationEvent {
    object NavigateToEmailVerification : NavigationEvent()
}

class CreateProfileViewModel(
    private val repo: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    var uiState by mutableStateOf(CreateProfileUiState())
        private set

    fun dismissDialog() {
        uiState = uiState.copy(showDialog = false, dialogMessage = "")
    }


    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onUsernameChange(newUsername: String) {
        val maxLength = 12
        val isError = newUsername.isBlank()
        if (newUsername.length <= maxLength) {
            uiState = uiState.copy(username = newUsername, isUsernameError = isError)
        }
    }

    fun onPasswordChange(newPassword: String) {
        val isError = newPassword.isNotEmpty() && (newPassword.length < 6 || newPassword.length > 12)
        uiState = uiState.copy(password = newPassword, isPasswordError = isError)
    }

    fun onEmailChange(newEmail: String) {
        val isError = newEmail.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()
        uiState = uiState.copy(email = newEmail, isEmailError = isError)
    }

    fun onSaveProfileClick(imageUri: Uri?) {
        if (uiState.isLoading) return

        if (uiState.username.isBlank() || uiState.password.isBlank() || uiState.email.isBlank() || uiState.isPasswordError || uiState.isEmailError) {
            println("Error: Campos inválidos.")
            uiState = uiState.copy(
                isUsernameError = uiState.username.isBlank(),
                isPasswordError = uiState.password.isBlank() || uiState.isPasswordError,
                isEmailError = uiState.email.isBlank() || uiState.isEmailError
            )
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)

                // Cerrar sesión antes de crear un nuevo usuario (si así lo desea la app)
                repo.signOut()

                // 1. Crear usuario en Firebase Auth
                val authResult = repo.createUser(uiState.email, uiState.password)
                val firebaseUser = authResult.user
                    ?: throw IllegalStateException("El usuario de Firebase es nulo después de la creación.")

                // 2. Enviar el correo de verificación
                repo.sendEmailVerification()

                var photoUrl: String? = null
                // Subir imagen a Firebase Storage si la URI no es nula y es válida
                println("[DEBUG] Usuario autenticado: ${'$'}{firebaseUser.uid}")
                println("[DEBUG] URI: $imageUri, scheme: ${'$'}{imageUri?.scheme}")
                if (imageUri != null) {
                    try {
                        photoUrl = repo.uploadProfileImage(imageUri, firebaseUser.uid)
                        println("[DEBUG] URL de imagen subida: ${'$'}photoUrl")
                    } catch (e: Exception) {
                        println("[ERROR] Error al subir la imagen: ${'$'}{e.message}")
                        photoUrl = ""
                    }
                } else {
                    println("[ERROR] No se seleccionó imagen de perfil.")
                }

                // 3. Guardar los datos del perfil en Firestore
                val userProfileData = hashMapOf(
                    "username" to uiState.username,
                    "email" to uiState.email,
                    "createdAt" to System.currentTimeMillis(),
                    "isEmailVerified" to false,
                    "photoUrl" to (photoUrl ?: "")
                )

                try {
                    repo.saveUserProfile(firebaseUser.uid, userProfileData)
                    println("[DEBUG] Usuario guardado en Firestore: ${'$'}{firebaseUser.uid}")
                } catch (e: Exception) {
                    println("[ERROR] Error al guardar usuario en Firestore: ${'$'}{e.message}")
                }

                uiState = uiState.copy(isLoading = false)

                // 4. Navegar a una pantalla que diga "Revisa tu correo para verificar la cuenta"
                _navigationEvent.emit(NavigationEvent.NavigateToEmailVerification)

            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false)
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                        "Ya existe una cuenta con este correo electrónico."
                    is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
                        "La contraseña es demasiado débil. Debe tener al menos 6 caracteres."
                    else ->
                        "Ocurrió un error inesperado: ${'$'}{e.message}"
                }
                uiState = uiState.copy(showDialog = true, dialogMessage = errorMessage)
            }
        }
    }

    fun checkAndUpdateEmailVerifiedStatus(onResult: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            user.reload().addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    if (user.isEmailVerified) {
                        // Actualizar solo el campo en Firestore
                        viewModelScope.launch {
                            try {
                                repo.updateUserProfileField(user.uid, "isEmailVerified", true)
                                println("[DEBUG] isEmailVerified actualizado en Firestore para ${user.uid}")
                            } catch (e: Exception) {
                                println("[ERROR] Error al actualizar isEmailVerified: ${e.message}")
                            }
                        }
                        onResult(true)
                    } else {
                        uiState = uiState.copy(showDialog = true, dialogMessage = "Debes verificar tu correo antes de continuar.")
                        onResult(false)
                    }
                } else {
                    println("[ERROR] Error al recargar usuario: ${reloadTask.exception?.message}")
                    uiState = uiState.copy(showDialog = true, dialogMessage = "Error al comprobar el estado del correo.")
                    onResult(false)
                }
            }
        } else {
            println("[ERROR] No hay usuario autenticado para verificar email.")
            uiState = uiState.copy(showDialog = true, dialogMessage = "No hay usuario autenticado.")
            onResult(false)
        }
    }
}
