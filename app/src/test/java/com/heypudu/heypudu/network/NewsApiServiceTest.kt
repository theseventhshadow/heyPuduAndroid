package com.heypudu.heypudu.network

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NewsApiServiceTest {

    @Test
    fun newsArticle_createdWithCorrectData() {
        // Arrange
        val title = "Concierto de Billie Eilish en Madrid"
        val source = NewsSource(id = "bbc-news", name = "BBC News")
        val description = "Billie Eilish anuncia concierto en Madrid"
        val url = "https://example.com/article"
        val publishedAt = "2025-12-06T10:00:00Z"

        // Act
        val article = NewsArticle(
            source = source,
            author = "Juan García",
            title = title,
            description = description,
            url = url,
            urlToImage = "https://example.com/image.jpg",
            publishedAt = publishedAt,
            content = "Contenido del artículo"
        )

        // Assert
        assertThat(article.title).isEqualTo(title)
        assertThat(article.source.name).isEqualTo("BBC News")
        assertThat(article.description).isEqualTo(description)
        assertThat(article.url).isEqualTo(url)
    }

    @Test
    fun newsArticlesResponse_containsCorrectArticles() {
        // Arrange
        val articles = listOf(
            NewsArticle(
                source = NewsSource(id = "1", name = "Source 1"),
                author = "Author 1",
                title = "Title 1",
                description = "Desc 1",
                url = "url1",
                urlToImage = null,
                publishedAt = "2025-12-06T10:00:00Z",
                content = "Content 1"
            ),
            NewsArticle(
                source = NewsSource(id = "2", name = "Source 2"),
                author = "Author 2",
                title = "Title 2",
                description = "Desc 2",
                url = "url2",
                urlToImage = null,
                publishedAt = "2025-12-06T11:00:00Z",
                content = "Content 2"
            )
        )

        // Act
        val response = NewsArticlesResponse(
            status = "ok",
            totalResults = 2,
            articles = articles
        )

        // Assert
        assertThat(response.articles).hasSize(2)
        assertThat(response.status).isEqualTo("ok")
        assertThat(response.totalResults).isEqualTo(2)
    }

    @Test
    fun newsSource_storesCorrectName() {
        // Arrange
        val name = "El País"

        // Act
        val source = NewsSource(id = "el-pais", name = name)

        // Assert
        assertThat(source.name).isEqualTo(name)
    }

    @Test
    fun newsArticle_withoutImage_isNull() {
        // Act
        val article = NewsArticle(
            source = NewsSource(id = "1", name = "Source"),
            author = "Author",
            title = "Title",
            description = "Description",
            url = "url",
            urlToImage = null,
            publishedAt = "2025-12-06T10:00:00Z",
            content = "Content"
        )

        // Assert
        assertThat(article.urlToImage).isNull()
    }
}

