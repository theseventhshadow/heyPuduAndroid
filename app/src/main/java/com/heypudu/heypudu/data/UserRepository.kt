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

    fun signOut() {
        auth.signOut()
    }

    @Suppress("unused")
    fun getCurrentUserId(): String? = auth.currentUser?.uid
}
