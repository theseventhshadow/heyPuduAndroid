package com.heypudu.heypudu.data

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    suspend fun createUser(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        // No hacemos signOut aquí: que lo controle el ViewModel si lo requiere
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun sendEmailVerification(): Unit = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: throw IllegalStateException("No authenticated user")
        user.sendEmailVerification().await()
    }

    suspend fun uploadProfileImage(imageUri: Uri, userId: String): String = withContext(Dispatchers.IO) {
        val imageRef = storage.reference.child("profile_images/$userId.jpg")
        imageRef.putFile(imageUri).await()
        imageRef.downloadUrl.await().toString()
    }

    suspend fun saveUserProfile(userId: String, data: Map<String, Any>): Unit = withContext(Dispatchers.IO) {
        firestore.collection("users").document(userId).set(data).await()
    }

    suspend fun updateUserProfileField(userId: String, field: String, value: Any): Unit = withContext(Dispatchers.IO) {
        firestore.collection("users").document(userId).update(field, value).await()
    }

    suspend fun savePost(post: Post): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = firestore.collection("posts").add(post).await()
            android.util.Log.d("UserRepository", "Post guardado correctamente con ID: ${result.id}")
            result.id
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error al guardar post: ${e.message}")
            null
        }
    }

    suspend fun uploadPostAudio(audioUri: Uri, postId: String): String = withContext(Dispatchers.IO) {
        val audioRef = storage.reference.child("post_audios/$postId.m4a")
        audioRef.putFile(audioUri).await()
        audioRef.downloadUrl.await().toString()
    }

    suspend fun incrementPlayCount(postId: String) = withContext(Dispatchers.IO) {
        val postRef = firestore.collection("posts").document(postId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val currentCount = snapshot.getLong("playCount") ?: 0L
            transaction.update(postRef, "playCount", currentCount + 1)
        }.await()
    }

    suspend fun toggleLikePost(postId: String, userId: String, liked: Boolean): Boolean = withContext(Dispatchers.IO) {
        val postRef = firestore.collection("posts").document(postId)
        return@withContext try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val likes = snapshot.get("likes") as? List<String> ?: emptyList()
                val updatedLikes = if (liked) {
                    if (!likes.contains(userId)) likes + userId else likes
                } else {
                    likes.filter { it != userId }
                }
                transaction.update(postRef, "likes", updatedLikes)
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }

    @Suppress("unused")
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun getPosts(onResult: (List<Post>) -> Unit) {
        firestore.collection("posts")
            .orderBy("publishedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    android.util.Log.e("UserRepository", "Error al obtener posts: "+error?.message)
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                android.util.Log.d("UserRepository", "Documentos recibidos: "+snapshot.documents.size)
                val posts = snapshot.documents.mapNotNull {
                    val post = it.toObject(Post::class.java)
                    if (post == null) {
                        android.util.Log.w("UserRepository", "Conversión fallida para documento: "+it.id)
                        android.util.Log.w("UserRepository", "Contenido bruto: "+it.data)
                    }
                    post?.copy(documentId = it.id)
                }
                android.util.Log.d("UserRepository", "Posts convertidos: "+posts.size)
                onResult(posts)
            }
    }

    fun getPostsOnce(onResult: (List<Post>) -> Unit) {
        firestore.collection("posts")
            .orderBy("publishedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.documents.mapNotNull {
                    val post = it.toObject(Post::class.java)
                    post?.copy(documentId = it.id)
                }
                onResult(posts)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}

data class Post(
    val documentId: String? = null,
    val authorId: String? = null,
    val authorUsername: String? = null,
    val authorPhotoUrl: String? = null,
    val publishedAt: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val audioUrl: String? = null,
    val likes: List<String>? = null,
    val comments: List<Comment>? = null,
    val playCount: Int? = null // contador de reproducciones
)

data class Comment(
    val commentId: String? = null,
    val authorId: String? = null,
    val text: String? = null,
    val audioUrl: String? = null,
    val createdAt: Long? = null
)
