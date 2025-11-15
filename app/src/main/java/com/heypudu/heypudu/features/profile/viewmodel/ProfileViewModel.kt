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
    private val _userPosts = MutableStateFlow<List<com.heypudu.heypudu.data.Post>>(emptyList())
    val userPosts: StateFlow<List<com.heypudu.heypudu.data.Post>> = _userPosts
    private val _likedPosts = MutableStateFlow<List<com.heypudu.heypudu.data.Post>>(emptyList())
    val likedPosts: StateFlow<List<com.heypudu.heypudu.data.Post>> = _likedPosts

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
                        // Cargar posts y likes después de obtener el usuario
                        loadUserPosts(userId)
                        loadLikedPosts(userId)
                    } else {
                        _error.value = "Usuario no encontrado."
                        _userPosts.value = emptyList()
                        _likedPosts.value = emptyList()
                    }
                }
                .addOnFailureListener { e ->
                    _error.value = "Error al cargar usuario: ${e.message}"
                    _userPosts.value = emptyList()
                    _likedPosts.value = emptyList()
                }
        }
    }

    private fun loadUserPosts(userId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            android.util.Log.d("ProfileViewModel", "Consultando posts del usuario: $userId")
            db.collection("posts")
                .whereEqualTo("authorId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val posts = result.documents.mapNotNull { it.toObject(com.heypudu.heypudu.data.Post::class.java) }
                    android.util.Log.d("ProfileViewModel", "Posts encontrados: ${posts.size}")
                    _userPosts.value = posts
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("ProfileViewModel", "Error al consultar posts: ${e.message}")
                    _userPosts.value = emptyList()
                }
        }
    }

    private fun loadLikedPosts(userId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            android.util.Log.d("ProfileViewModel", "Consultando likes del usuario: $userId")
            db.collection("posts")
                .whereArrayContains("likes", userId)
                .get()
                .addOnSuccessListener { result ->
                    val posts = result.documents.mapNotNull { it.toObject(com.heypudu.heypudu.data.Post::class.java) }
                    android.util.Log.d("ProfileViewModel", "Posts con likes encontrados: ${posts.size}")
                    _likedPosts.value = posts
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("ProfileViewModel", "Error al consultar likes: ${e.message}")
                    _likedPosts.value = emptyList()
                }
        }
    }
}
