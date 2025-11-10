package com.heypudu.heypudu.features.mainscreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heypudu.heypudu.data.Post
import com.heypudu.heypudu.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    repo: UserRepository = UserRepository()
) : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    val repo: UserRepository = repo

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        repo.getPosts { postsList ->
            _posts.value = postsList
        }
    }

    fun setPosts(posts: List<Post>) {
        _posts.value = posts
    }

    fun createPost(post: Post, audioUri: android.net.Uri?, onComplete: () -> Unit) {
        viewModelScope.launch {
            var audioUrl = ""
            if (audioUri != null) {
                val postId = "${post.authorId}_${post.publishedAt}"
                audioUrl = repo.uploadPostAudio(audioUri, postId)
            }
            val finalPost = post.copy(audioUrl = audioUrl)
            repo.savePost(finalPost)
            onComplete()
        }
    }

    fun signOut() {
        repo.signOut()
    }
}
