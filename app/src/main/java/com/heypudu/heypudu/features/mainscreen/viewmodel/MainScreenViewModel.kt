package com.heypudu.heypudu.features.mainscreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heypudu.heypudu.data.Post
import com.heypudu.heypudu.data.UserRepository
import com.heypudu.heypudu.ui.components.AudioPlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.platform.LocalContext

class MainScreenViewModel(
    val repo: UserRepository = UserRepository()
) : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts
    private var forceRefresh = false

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        repo.getPosts { postsList ->
            if (!forceRefresh) {
                _posts.value = postsList
            }
        }
    }

    fun setPosts(posts: List<Post>) {
        _posts.value = posts
        forceRefresh = true
        // Desactivar el flag después de un breve tiempo para permitir futuras actualizaciones en tiempo real
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            forceRefresh = false
        }
    }

    fun createPost(
        title: String,
        content: String,
        audioUri: android.net.Uri?,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val userId = repo.getCurrentUserId() ?: return@launch
            val userDoc = Firebase.firestore.collection("users").document(userId).get().await()
            val authorUsername = userDoc.getString("username") ?: ""
            val authorPhotoUrl = userDoc.getString("photoUrl") ?: ""
            val publishedAt = System.currentTimeMillis()
            var audioUrl = ""
            if (audioUri != null) {
                val postId = "${userId}_$publishedAt"
                audioUrl = repo.uploadPostAudio(audioUri, postId)
                // Si necesitas calcular la duración, hazlo en la UI y pásala como parámetro
            }
            val post = Post(
                authorId = userId,
                authorUsername = authorUsername,
                authorPhotoUrl = authorPhotoUrl,
                publishedAt = publishedAt,
                title = title,
                content = content,
                audioUrl = audioUrl,
                likes = emptyList(),
                comments = emptyList(),
                playCount = 0,
                documentId = null
            )
            repo.savePost(post)
            onComplete()
        }
    }

    fun toggleLikePost(postId: String, userId: String, liked: Boolean, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repo.toggleLikePost(postId, userId, liked)
            if (result) {
                // Actualizar la lista de posts tras el cambio
                repo.getPostsOnce { postsList ->
                    setPosts(postsList)
                }
            }
            onResult(result)
        }
    }

    fun signOut(onSignOutComplete: (() -> Unit)? = null) {
        AudioPlayerController.stop()
        repo.signOut()
        // Esperar a que el estado de autenticación se actualice
        kotlinx.coroutines.GlobalScope.launch {
            kotlinx.coroutines.delay(300)
            onSignOutComplete?.invoke()
        }
    }
}
