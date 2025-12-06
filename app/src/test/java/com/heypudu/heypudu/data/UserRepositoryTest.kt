package com.heypudu.heypudu.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UserRepositoryTest {

    @Test
    fun post_isCreatedWithCorrectData() {
        // Arrange
        val authorId = "user123"
        val title = "Mi primer post"
        val content = "Contenido del post"
        val audioUrl = "https://example.com/audio.m4a"
        val publishedAt = System.currentTimeMillis()

        // Act
        val post = Post(
            authorId = authorId,
            title = title,
            content = content,
            audioUrl = audioUrl,
            publishedAt = publishedAt,
            likes = emptyList(),
            playCount = 0
        )

        // Assert
        assertThat(post.authorId).isEqualTo(authorId)
        assertThat(post.title).isEqualTo(title)
        assertThat(post.content).isEqualTo(content)
        assertThat(post.audioUrl).isEqualTo(audioUrl)
        assertThat(post.publishedAt).isEqualTo(publishedAt)
    }

    @Test
    fun post_withLikes_hasCorrectLikesList() {
        // Arrange
        val likesList = listOf("user1", "user2", "user3")

        // Act
        val post = Post(
            authorId = "user123",
            title = "Post",
            content = "Content",
            likes = likesList
        )

        // Assert
        assertThat(post.likes).hasSize(3)
        assertThat(post.likes).containsExactly("user1", "user2", "user3")
    }

    @Test
    fun post_withoutLikes_isNull() {
        // Act
        val post = Post(
            authorId = "user123",
            title = "Post",
            content = "Content"
        )

        // Assert
        assertThat(post.likes).isNull()
    }

    @Test
    fun post_withPlayCount_storesCorrectValue() {
        // Arrange
        val playCount = 42

        // Act
        val post = Post(
            authorId = "user123",
            title = "Post",
            content = "Content",
            playCount = playCount
        )

        // Assert
        assertThat(post.playCount).isEqualTo(playCount)
    }

    @Test
    fun comment_isCreatedWithCorrectData() {
        // Arrange
        val authorId = "user456"
        val text = "Comentario genial!"
        val createdAt = System.currentTimeMillis()

        // Act
        val comment = Comment(
            authorId = authorId,
            text = text,
            createdAt = createdAt
        )

        // Assert
        assertThat(comment.authorId).isEqualTo(authorId)
        assertThat(comment.text).isEqualTo(text)
        assertThat(comment.createdAt).isEqualTo(createdAt)
    }

    @Test
    fun post_withDocumentId_storesId() {
        // Arrange
        val docId = "doc123abc"

        // Act
        val post = Post(
            documentId = docId,
            authorId = "user123",
            title = "Post con ID"
        )

        // Assert
        assertThat(post.documentId).isEqualTo(docId)
    }

    @Test
    fun post_withUsername_storesUsername() {
        // Arrange
        val username = "pedrolopez"

        // Act
        val post = Post(
            authorId = "user123",
            authorUsername = username,
            title = "Post",
            content = "Contenido"
        )

        // Assert
        assertThat(post.authorUsername).isEqualTo(username)
    }

    @Test
    fun post_withPhotoUrl_storesPhotoUrl() {
        // Arrange
        val photoUrl = "https://example.com/photo.jpg"

        // Act
        val post = Post(
            authorId = "user123",
            authorPhotoUrl = photoUrl,
            title = "Post"
        )

        // Assert
        assertThat(post.authorPhotoUrl).isEqualTo(photoUrl)
    }

    @Test
    fun comment_withoutAudioUrl_isNull() {
        // Act
        val comment = Comment(
            authorId = "user123",
            text = "Comentario sin audio"
        )

        // Assert
        assertThat(comment.audioUrl).isNull()
    }

    @Test
    fun post_multiplePostsHaveDifferentData() {
        // Arrange & Act
        val post1 = Post(
            authorId = "user1",
            title = "Post 1",
            content = "Content 1"
        )
        val post2 = Post(
            authorId = "user2",
            title = "Post 2",
            content = "Content 2"
        )

        // Assert
        assertThat(post1.authorId).isNotEqualTo(post2.authorId)
        assertThat(post1.title).isNotEqualTo(post2.title)
        assertThat(post1.content).isNotEqualTo(post2.content)
    }
}

