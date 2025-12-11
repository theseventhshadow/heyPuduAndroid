package com.heypudu.heypudu.data

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import AppDatabase
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.content.Context
import com.heypudu.heypudu.utils.UploadStateManager
import kotlinx.serialization.Serializable

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

    suspend fun uploadProfilePhoto(imageUri: Uri, userId: String): String = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val imageRef = storage.reference.child("profile_photos/$userId/$timestamp.jpg")
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
        return@withContext try {
            val audioRef = storage.reference.child("post_audios/$postId.m4a")

            // Actualizar progreso: preparando
            UploadStateManager.updateProgress(15f, "Preparando audio...")

            // Actualizar progreso: comprimiendo
            UploadStateManager.updateProgress(30f, "Comprimiendo...")

            // Subir archivo con seguimiento de progreso
            val uploadTask = audioRef.putFile(audioUri)

            // Listener para el progreso de subida
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                val adjustedProgress = 30 + (progress * 0.6).toFloat() // Del 30% al 90%
                UploadStateManager.updateProgress(adjustedProgress, "Subiendo... ${adjustedProgress.toInt()}%")
            }

            // Esperar a que se complete
            uploadTask.await()

            // Actualizar progreso: obteniendo URL
            UploadStateManager.updateProgress(95f, "Finalizando...")

            // Obtener URL de descarga
            val downloadUrl = audioRef.downloadUrl.await().toString()

            UploadStateManager.updateProgress(100f, "¡Publicado!")

            downloadUrl
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error al subir audio: ${e.message}")
            UploadStateManager.updateProgress(0f, "Error en la subida")
            ""
        }
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

    suspend fun clearLocalCache(context: Context) = withContext(Dispatchers.IO) {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
        val deletedRows = db.cachedPostDao().clearAll()
        android.util.Log.d("UserRepository", "clearAll ejecutado, filas eliminadas: $deletedRows")
        db.close()
        val cacheDir = context.cacheDir
        cacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("audio_") || file.name.startsWith("profile_")) {
                val deleted = file.delete()
                android.util.Log.d("UserRepository", "Archivo ${file.name} eliminado: $deleted")
            }
        }
        val remainingFiles = cacheDir.listFiles()?.map { it.name } ?: emptyList()
        android.util.Log.d("UserRepository", "Archivos restantes en caché: $remainingFiles")
    }

    // ======== FUNCIONES PARA LANZAMIENTOS (ÁLBUMES Y PODCASTS) ========

    suspend fun createAlbum(album: com.heypudu.heypudu.data.models.Album): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = firestore.collection("albums").add(album).await()
            android.util.Log.d("UserRepository", "Álbum creado: ${result.id}")
            result.id
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error al crear álbum: ${e.message}")
            null
        }
    }

    suspend fun createPodcast(podcast: com.heypudu.heypudu.data.models.Podcast): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = firestore.collection("podcasts").add(podcast).await()
            android.util.Log.d("UserRepository", "Podcast creado: ${result.id}")
            result.id
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error al crear podcast: ${e.message}")
            null
        }
    }

    fun getAlbumsByUser(userId: String, onResult: (List<com.heypudu.heypudu.data.models.Album>) -> Unit) {
        firestore.collection("albums")
            .whereEqualTo("artistId", userId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    android.util.Log.e("UserRepository", "Error al obtener álbumes: ${error?.message}")
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val albums = snapshot.documents.mapNotNull {
                    val album = it.toObject(com.heypudu.heypudu.data.models.Album::class.java)
                    album?.copy(albumId = it.id)
                }
                android.util.Log.d("UserRepository", "Álbumes obtenidos: ${albums.size}")
                onResult(albums)
            }
    }

    fun getPodcastsByUser(userId: String, onResult: (List<com.heypudu.heypudu.data.models.Podcast>) -> Unit) {
        firestore.collection("podcasts")
            .whereEqualTo("creatorId", userId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    android.util.Log.e("UserRepository", "Error al obtener podcasts: ${error?.message}")
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val podcasts = snapshot.documents.mapNotNull {
                    val podcast = it.toObject(com.heypudu.heypudu.data.models.Podcast::class.java)
                    podcast?.copy(podcastId = it.id)
                }
                android.util.Log.d("UserRepository", "Podcasts obtenidos: ${podcasts.size}")
                onResult(podcasts)
            }
    }

    fun getAllPublicReleases(onResult: (List<Any>) -> Unit) {
        firestore.collection("albums")
            .whereEqualTo("isPublished", true)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    android.util.Log.e("UserRepository", "Error al obtener lanzamientos: ${error?.message}")
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val releases = snapshot.documents.mapNotNull {
                    it.toObject(com.heypudu.heypudu.data.models.Album::class.java)
                }
                android.util.Log.d("UserRepository", "Lanzamientos públicos obtenidos: ${releases.size}")
                onResult(releases)
            }
    }

    suspend fun uploadReleaseCover(imageUri: android.net.Uri, releaseId: String, type: String): String = withContext(Dispatchers.IO) {
        val coverRef = storage.reference.child("release_covers/$type/$releaseId.jpg")
        coverRef.putFile(imageUri).await()
        return@withContext coverRef.downloadUrl.await().toString()
    }

    suspend fun getPostById(postId: String): Post? = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = firestore.collection("posts").document(postId).get().await()
            val post = snapshot.toObject(Post::class.java)
            post?.copy(documentId = snapshot.id)
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error al obtener post: ${e.message}")
            null
        }
    }
}

@Serializable
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

@Serializable
data class Comment(
    val commentId: String? = null,
    val authorId: String? = null,
    val text: String? = null,
    val audioUrl: String? = null,
    val createdAt: Long? = null
)
