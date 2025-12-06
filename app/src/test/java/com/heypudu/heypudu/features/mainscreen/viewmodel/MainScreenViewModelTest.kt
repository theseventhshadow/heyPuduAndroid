package com.heypudu.heypudu.features.mainscreen.viewmodel

import com.heypudu.heypudu.data.Post
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MainScreenViewModelTest {

    @Test
    fun post_createsPostWithTitle() {
        // Arrange
        val title = "Mi primer post de audio"

        // Act
        val post = Post(
            authorId = "user123",
            title = title,
            content = "Contenido del post"
        )

        // Assert
        assertThat(post.title).isEqualTo(title)
    }

    @Test
    fun post_createsPostWithAuthorId() {
        // Arrange
        val authorId = "user456"

        // Act
        val post = Post(
            authorId = authorId,
            title = "Post"
        )

        // Assert
        assertThat(post.authorId).isEqualTo(authorId)
    }

    @Test
    fun post_withAudioUrl_storesCorrectly() {
        // Arrange
        val audioUrl = "https://firebase.storage/audio123.m4a"

        // Act
        val post = Post(
            authorId = "user123",
            title = "Post con audio",
            audioUrl = audioUrl
        )

        // Assert
        assertThat(post.audioUrl).isEqualTo(audioUrl)
    }

    @Test
    fun post_withLikes_containsMultipleUsers() {
        // Arrange
        val likes = listOf("user1", "user2", "user3", "user4", "user5")

        // Act
        val post = Post(
            authorId = "user123",
            title = "Post popular",
            likes = likes
        )

        // Assert
        assertThat(post.likes).hasSize(5)
        assertThat(post.likes).contains("user1")
        assertThat(post.likes).contains("user5")
    }

    @Test
    fun post_playCount_startsAtZero() {
        // Act
        val post = Post(
            authorId = "user123",
            title = "Post",
            playCount = 0
        )

        // Assert
        assertThat(post.playCount).isEqualTo(0)
    }

    @Test
    fun post_playCount_increments() {
        // Arrange
        val initialCount = 10

        // Act
        val post = Post(
            authorId = "user123",
            title = "Post",
            playCount = initialCount
        )
        val newCount = post.playCount?.plus(1) ?: 0

        // Assert
        assertThat(newCount).isEqualTo(11)
    }

    @Test
    fun post_withPublishedAt_storesTimestamp() {
        // Arrange
        val timestamp = System.currentTimeMillis()

        // Act
        val post = Post(
            authorId = "user123",
            title = "Post",
            publishedAt = timestamp
        )

        // Assert
        assertThat(post.publishedAt).isEqualTo(timestamp)
    }

    @Test
    fun post_withUserMetadata_storesCorrectly() {
        // Arrange
        val username = "pedrolopez"
        val photoUrl = "https://example.com/photo.jpg"

        // Act
        val post = Post(
            authorId = "user123",
            authorUsername = username,
            authorPhotoUrl = photoUrl,
            title = "Post"
        )

        // Assert
        assertThat(post.authorUsername).isEqualTo(username)
        assertThat(post.authorPhotoUrl).isEqualTo(photoUrl)
    }

    @Test
    fun post_isEmpty_whenFieldsAreNull() {
        // Act
        val post = Post()

        // Assert
        assertThat(post.authorId).isNull()
        assertThat(post.title).isNull()
        assertThat(post.content).isNull()
    }
}

