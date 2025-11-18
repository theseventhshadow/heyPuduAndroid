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
import android.content.Context
import androidx.room.Room
import AppDatabase

class MainScreenViewModel(
    val repo: UserRepository = UserRepository(),
    private val appContext: Context? = null // Se puede pasar desde la Activity
) : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts
    private var forceRefresh = false

    private val _currentPlayingPost = MutableStateFlow<Post?>(null)
    val currentPlayingPost: StateFlow<Post?> = _currentPlayingPost

    private val db: AppDatabase? = appContext?.let {
        Room.databaseBuilder(
            it,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

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

    fun setCurrentPlayingPost(post: Post?) {
        _currentPlayingPost.value = post
    }

    private fun clearCacheFiles() {
        appContext?.let { context ->
            val cacheDir = context.cacheDir
            cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("audio_") || file.name.startsWith("profile_")) {
                    try {
                        file.delete()
                    } catch (e: Exception) {
                        android.util.Log.e("MainScreenViewModel", "Error eliminando archivo: ${file.name}")
                    }
                }
            }
        }
    }

    fun signOut(onSignOutComplete: (() -> Unit)? = null) {
        AudioPlayerController.stop()
        repo.signOut()
        viewModelScope.launch {
            appContext?.let { ctx ->
                repo.clearLocalCache(ctx)
            }
            kotlinx.coroutines.delay(300)
            onSignOutComplete?.invoke()
        }
    }
}
