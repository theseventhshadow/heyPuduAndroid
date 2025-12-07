package com.heypudu.heypudu.features.profile.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProfileViewModelTest {

    @Test
    fun album_createdWithTitle() {
        // Arrange
        val title = "Mi primer álbum"

        // Act
        val album = com.heypudu.heypudu.data.models.Album(
            title = title,
            artistId = "artist123"
        )

        // Assert
        assertThat(album.title).isEqualTo(title)
    }

    @Test
    fun album_withDescription_storesCorrectly() {
        // Arrange
        val description = "Descripción del álbum"

        // Act
        val album = com.heypudu.heypudu.data.models.Album(
            title = "Álbum",
            artistId = "artist123",
            description = description
        )

        // Assert
        assertThat(album.description).isEqualTo(description)
    }

    @Test
    fun podcast_createdWithTitle() {
        // Arrange
        val title = "Mi primer podcast"

        // Act
        val podcast = com.heypudu.heypudu.data.models.Podcast(
            title = title,
            creatorId = "creator123"
        )

        // Assert
        assertThat(podcast.title).isEqualTo(title)
    }

    @Test
    fun album_withPublishStatus_storesCorrectly() {
        // Arrange
        val isPublished = true

        // Act
        val album = com.heypudu.heypudu.data.models.Album(
            title = "Álbum Publicado",
            artistId = "artist123",
            isPublished = isPublished
        )

        // Assert
        assertThat(album.isPublished).isTrue()
    }

    @Test
    fun podcast_withCreatorId_storesCorrectly() {
        // Arrange
        val creatorId = "podcaster123"

        // Act
        val podcast = com.heypudu.heypudu.data.models.Podcast(
            title = "Podcast",
            creatorId = creatorId
        )

        // Assert
        assertThat(podcast.creatorId).isEqualTo(creatorId)
    }

    @Test
    fun album_withCoverUrl_storesCorrectly() {
        // Arrange
        val coverUrl = "https://example.com/cover.jpg"

        // Act
        val album = com.heypudu.heypudu.data.models.Album(
            title = "Álbum con portada",
            artistId = "artist123",
            coverUrl = coverUrl
        )

        // Assert
        assertThat(album.coverUrl).isEqualTo(coverUrl)
    }

    @Test
    fun podcast_multipleCreated_haveDifferentIds() {
        // Act
        val podcast1 = com.heypudu.heypudu.data.models.Podcast(
            podcastId = "podcast1",
            title = "Podcast 1",
            creatorId = "creator1"
        )
        val podcast2 = com.heypudu.heypudu.data.models.Podcast(
            podcastId = "podcast2",
            title = "Podcast 2",
            creatorId = "creator2"
        )

        // Assert
        assertThat(podcast1.podcastId).isNotEqualTo(podcast2.podcastId)
        assertThat(podcast1.creatorId).isNotEqualTo(podcast2.creatorId)
    }
}

