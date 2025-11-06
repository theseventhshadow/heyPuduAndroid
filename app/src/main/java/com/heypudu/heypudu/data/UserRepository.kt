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

    suspend fun savePost(post: Post): Unit = withContext(Dispatchers.IO) {
        firestore.collection("posts").add(post).await()
    }

    fun signOut() {
        auth.signOut()
    }

    @Suppress("unused")
    fun getCurrentUserId(): String? = auth.currentUser?.uid
}

data class Post(
    val authorId: String = "",
    val authorUsername: String = "",
    val authorPhotoUrl: String = "",
    val publishedAt: Long = System.currentTimeMillis(),
    val message: String = "",
    val audioUrl: String = "",
    val likes: List<String> = emptyList(),
    val comments: List<Comment> = emptyList()
)

data class Comment(
    val commentId: String = "",
    val authorId: String = "",
    val text: String = "",
    val audioUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
