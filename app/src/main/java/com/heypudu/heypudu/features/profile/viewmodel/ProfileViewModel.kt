package com.heypudu.heypudu.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {
    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> = _photoUrl
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username
    private val _email = MutableStateFlow<String?>(null)
    val email: StateFlow<String?> = _email
    private val _postCount = MutableStateFlow(0)
    val postCount: StateFlow<Int> = _postCount
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUser(userId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        _photoUrl.value = doc.getString("photoUrl")
                        _username.value = doc.getString("username")
                        _email.value = doc.getString("email")
                        _postCount.value = doc.getLong("postCount")?.toInt() ?: 0
                        _error.value = null
                    } else {
                        _error.value = "Usuario no encontrado."
                    }
                }
                .addOnFailureListener { e ->
                    _error.value = "Error al cargar usuario: ${e.message}"
                }
        }
    }
}
