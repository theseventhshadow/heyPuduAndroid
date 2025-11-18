package com.heypudu.heypudu.data.models

data class Release(
    val releaseId: String = "",
    val creatorId: String = "",
    val creatorUsername: String = "",
    val creatorPhotoUrl: String = "",
    val title: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val releaseType: String = "", // "album" o "podcast"
    val releaseDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPublished: Boolean = false,
    val totalItems: Int = 0, // total de canciones o episodios
    val likes: List<String> = emptyList(),
    val playCount: Int = 0
)

data class Album(
    val albumId: String = "",
    val artistId: String = "",
    val artistUsername: String = "",
    val artistPhotoUrl: String = "",
    val title: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val genre: String = "",
    val releaseDate: Long = System.currentTimeMillis(),
    val totalTracks: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPublished: Boolean = false,
    val likes: List<String> = emptyList()
)

data class Track(
    val trackId: String = "",
    val albumId: String = "",
    val title: String = "",
    val description: String = "",
    val audioUrl: String = "",
    val duration: Long = 0,
    val trackNumber: Int = 0,
    val releaseDate: Long = System.currentTimeMillis(),
    val playCount: Int = 0,
    val likes: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Podcast(
    val podcastId: String = "",
    val creatorId: String = "",
    val creatorUsername: String = "",
    val creatorPhotoUrl: String = "",
    val title: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val category: String = "",
    val language: String = "es",
    val frequency: String = "",
    val releaseDate: Long = System.currentTimeMillis(),
    val totalSeasons: Int = 0,
    val totalEpisodes: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPublished: Boolean = false,
    val likes: List<String> = emptyList()
)

data class Season(
    val seasonId: String = "",
    val podcastId: String = "",
    val seasonNumber: Int = 0,
    val title: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val releaseDate: Long = System.currentTimeMillis(),
    val totalEpisodes: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isPublished: Boolean = false
)

data class Episode(
    val episodeId: String = "",
    val podcastId: String = "",
    val seasonId: String = "",
    val episodeNumber: Int = 0,
    val globalEpisodeNumber: Int = 0,
    val title: String = "",
    val description: String = "",
    val audioUrl: String = "",
    val duration: Long = 0,
    val releaseDate: Long = System.currentTimeMillis(),
    val playCount: Int = 0,
    val likes: List<String> = emptyList(),
    val transcript: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

