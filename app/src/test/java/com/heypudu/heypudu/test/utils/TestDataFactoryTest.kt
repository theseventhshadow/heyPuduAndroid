package com.heypudu.heypudu.test.utils

import com.heypudu.heypudu.data.Post
import com.heypudu.heypudu.data.Comment
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TestDataFactoryTest {

    @Test
    fun createPost_withBasicData_isNotNull() {
        // Act
        val post = Post(
            authorId = "user123",
            title = "Test",
            content = "Content"
        )

        // Assert
        assertThat(post).isNotNull()
    }

    @Test
    fun createPost_withAllFields_hasCorrectValues() {
        // Arrange
        val authorId = "user123"
        val title = "Post"
        val content = "Content"
        val audioUrl = "https://example.com/audio.m4a"

        // Act
        val post = Post(
            authorId = authorId,
            title = title,
            content = content,
            audioUrl = audioUrl
        )

        // Assert
        assertThat(post.authorId).isEqualTo(authorId)
        assertThat(post.title).isEqualTo(title)
        assertThat(post.content).isEqualTo(content)
        assertThat(post.audioUrl).isEqualTo(audioUrl)
    }

    @Test
    fun createComment_withData_isNotNull() {
        // Act
        val comment = Comment(
            authorId = "user123",
            text = "Great!"
        )

        // Assert
        assertThat(comment).isNotNull()
    }

    @Test
    fun createComment_withAllFields_hasCorrectValues() {
        // Arrange
        val authorId = "user123"
        val text = "Comment text"
        val timestamp = System.currentTimeMillis()

        // Act
        val comment = Comment(
            authorId = authorId,
            text = text,
            createdAt = timestamp
        )

        // Assert
        assertThat(comment.authorId).isEqualTo(authorId)
        assertThat(comment.text).isEqualTo(text)
        assertThat(comment.createdAt).isEqualTo(timestamp)
    }

    @Test
    fun createAlbum_withData_isNotNull() {
        // Act
        val album = com.heypudu.heypudu.data.models.Album(
            title = "Album",
            artistId = "artist123"
        )

        // Assert
        assertThat(album).isNotNull()
    }

    @Test
    fun createPodcast_withData_isNotNull() {
        // Act
        val podcast = com.heypudu.heypudu.data.models.Podcast(
            title = "Podcast",
            creatorId = "creator123"
        )

        // Assert
        assertThat(podcast).isNotNull()
    }
}

