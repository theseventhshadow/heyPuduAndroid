package com.heypudu.heypudu.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.heypudu.heypudu.data.UserRepository

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
    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing
    private val _followerCount = MutableStateFlow(0)
    val followerCount: StateFlow<Int> = _followerCount

    // Estados para lanzamientos
    private val _userAlbums = MutableStateFlow<List<com.heypudu.heypudu.data.models.Album>>(emptyList())
    val userAlbums: StateFlow<List<com.heypudu.heypudu.data.models.Album>> = _userAlbums
    private val _userPodcasts = MutableStateFlow<List<com.heypudu.heypudu.data.models.Podcast>>(emptyList())
    val userPodcasts: StateFlow<List<com.heypudu.heypudu.data.models.Podcast>> = _userPodcasts
    private val repo = UserRepository()

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
                        _followerCount.value = (doc.get("followers") as? List<*>)?.size ?: 0
                        _error.value = null
                        // Cargar posts, likes y lanzamientos después de obtener el usuario
                        loadUserPosts(userId)
                        loadLikedPosts(userId)
                        loadUserAlbums(userId)
                        loadUserPodcasts(userId)
                        checkIfFollowing(userId)
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
                    _postCount.value = posts.size // Actualiza el contador de posts
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("ProfileViewModel", "Error al consultar posts: ${e.message}")
                    _userPosts.value = emptyList()
                    _postCount.value = 0 // Si falla, contador en 0
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

    private fun checkIfFollowing(userId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener { doc ->
                    val following = (doc.get("following") as? List<String>) ?: emptyList()
                    _isFollowing.value = following.contains(userId)
                }
                .addOnFailureListener {
                    _isFollowing.value = false
                }
        }
    }

    fun toggleFollowUser(userId: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                val currentFollowing = _isFollowing.value
                android.util.Log.d("ProfileViewModel", "toggleFollowUser: userId=$userId, currentFollowing=$currentFollowing")

                if (currentFollowing) {
                    // Dejar de seguir
                    android.util.Log.d("ProfileViewModel", "Dejando de seguir a $userId")
                    db.collection("users").document(currentUserId)
                        .update("following", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                        .addOnSuccessListener {
                            android.util.Log.d("ProfileViewModel", "following actualizado correctamente")
                        }
                        .addOnFailureListener { e ->
                            android.util.Log.e("ProfileViewModel", "Error al actualizar following: ${e.message}")
                        }

                    db.collection("users").document(userId)
                        .update("followers", com.google.firebase.firestore.FieldValue.arrayRemove(currentUserId))
                        .addOnSuccessListener {
                            android.util.Log.d("ProfileViewModel", "followers actualizado correctamente")
                            _isFollowing.value = false
                            reloadFollowerCount(userId)
                        }
                        .addOnFailureListener { e ->
                            android.util.Log.e("ProfileViewModel", "Error al actualizar followers: ${e.message}")
                        }
                } else {
                    // Seguir
                    android.util.Log.d("ProfileViewModel", "Siguiendo a $userId")
                    db.collection("users").document(currentUserId)
                        .update("following", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                        .addOnSuccessListener {
                            android.util.Log.d("ProfileViewModel", "following actualizado correctamente")
                        }
                        .addOnFailureListener { e ->
                            android.util.Log.e("ProfileViewModel", "Error al actualizar following: ${e.message}")
                        }

                    db.collection("users").document(userId)
                        .update("followers", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId))
                        .addOnSuccessListener {
                            android.util.Log.d("ProfileViewModel", "followers actualizado correctamente")
                            _isFollowing.value = true
                            reloadFollowerCount(userId)
                        }
                        .addOnFailureListener { e ->
                            android.util.Log.e("ProfileViewModel", "Error al actualizar followers: ${e.message}")
                        }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Exception en toggleFollowUser: ${e.message}")
            }
        }
    }

    private fun reloadFollowerCount(userId: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                android.util.Log.d("ProfileViewModel", "Recargando contador de seguidores para $userId")
                // Esperar un poco para que Firestore procese la actualización
                kotlinx.coroutines.delay(500)
                db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            val followers = (doc.get("followers") as? List<*>) ?: emptyList<Any>()
                            val newCount = followers.size
                            android.util.Log.d("ProfileViewModel", "Contador anterior: ${_followerCount.value}, Contador nuevo: $newCount")
                            _followerCount.value = newCount
                            android.util.Log.d("ProfileViewModel", "Contador de seguidores actualizado: $newCount")
                        }
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("ProfileViewModel", "Error al recargar contador: ${e.message}")
                    }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Exception en reloadFollowerCount: ${e.message}")
            }
        }
    }

    // ======== MÉTODOS PARA LANZAMIENTOS ========

    fun createAlbum(album: com.heypudu.heypudu.data.models.Album) {
        viewModelScope.launch {
            try {
                val albumId = repo.createAlbum(album)
                if (albumId != null) {
                    android.util.Log.d("ProfileViewModel", "Álbum creado exitosamente: $albumId")
                    // Recargar álbumes del usuario
                    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        loadUserAlbums(userId)
                    }
                } else {
                    android.util.Log.e("ProfileViewModel", "Error al crear álbum")
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Exception al crear álbum: ${e.message}")
            }
        }
    }

    fun createPodcast(podcast: com.heypudu.heypudu.data.models.Podcast) {
        viewModelScope.launch {
            try {
                val podcastId = repo.createPodcast(podcast)
                if (podcastId != null) {
                    android.util.Log.d("ProfileViewModel", "Podcast creado exitosamente: $podcastId")
                    // Recargar podcasts del usuario
                    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        loadUserPodcasts(userId)
                    }
                } else {
                    android.util.Log.e("ProfileViewModel", "Error al crear podcast")
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Exception al crear podcast: ${e.message}")
            }
        }
    }

    fun loadUserAlbums(userId: String) {
        repo.getAlbumsByUser(userId) { albums ->
            _userAlbums.value = albums
            android.util.Log.d("ProfileViewModel", "Álbumes cargados: ${albums.size}")
        }
    }

    fun loadUserPodcasts(userId: String) {
        repo.getPodcastsByUser(userId) { podcasts ->
            _userPodcasts.value = podcasts
            android.util.Log.d("ProfileViewModel", "Podcasts cargados: ${podcasts.size}")
        }
    }
}
