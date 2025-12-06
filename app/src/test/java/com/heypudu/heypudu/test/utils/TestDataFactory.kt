package com.heypudu.heypudu.test.utils

import com.heypudu.heypudu.data.Post
import com.heypudu.heypudu.data.Comment
import com.heypudu.heypudu.data.models.Album
import com.heypudu.heypudu.data.models.Podcast

/**
 * Test utilities and factory methods for creating mock data
 */

object TestDataFactory {

    fun createPost(
        documentId: String = "post_${System.currentTimeMillis()}",
        authorId: String = "user123",
        authorUsername: String = "testuser",
        title: String = "Test Post",
        content: String = "Test content",
        audioUrl: String? = null,
        likes: List<String> = emptyList(),
        playCount: Int = 0,
        publishedAt: Long = System.currentTimeMillis()
    ): Post = Post(
        documentId = documentId,
        authorId = authorId,
        authorUsername = authorUsername,
        title = title,
        content = content,
        audioUrl = audioUrl,
        likes = likes,
        playCount = playCount,
        publishedAt = publishedAt
    )

    fun createComment(
        commentId: String = "comment_${System.currentTimeMillis()}",
        authorId: String = "user123",
        text: String = "Test comment",
        audioUrl: String? = null,
        createdAt: Long = System.currentTimeMillis()
    ): Comment = Comment(
        commentId = commentId,
        authorId = authorId,
        text = text,
        audioUrl = audioUrl,
        createdAt = createdAt
    )

    fun createAlbum(
        albumId: String = "album_${System.currentTimeMillis()}",
        title: String = "Test Album",
        artistId: String = "artist123",
        description: String = "Test Description",
        coverUrl: String = "",
        isPublished: Boolean = false,
        createdAt: Long = System.currentTimeMillis()
    ): Album = Album(
        albumId = albumId,
        title = title,
        artistId = artistId,
        description = description,
        coverUrl = coverUrl,
        isPublished = isPublished,
        createdAt = createdAt
    )

    fun createPodcast(
        podcastId: String = "podcast_${System.currentTimeMillis()}",
        title: String = "Test Podcast",
        creatorId: String = "creator123",
        description: String = "Test Description",
        coverUrl: String = "",
        isPublished: Boolean = false,
        createdAt: Long = System.currentTimeMillis()
    ): Podcast = Podcast(
        podcastId = podcastId,
        title = title,
        creatorId = creatorId,
        description = description,
        coverUrl = coverUrl,
        isPublished = isPublished,
        createdAt = createdAt
    )

    fun createPostList(count: Int = 5): List<Post> =
        (1..count).map { i ->
            createPost(
                documentId = "post_$i",
                title = "Post $i",
                content = "Content for post $i"
            )
        }

    fun createAlbumList(count: Int = 5): List<Album> =
        (1..count).map { i ->
            createAlbum(
                albumId = "album_$i",
                title = "Album $i"
            )
        }
}

