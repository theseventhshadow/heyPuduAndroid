package com.heypudu.heypudu.data

import android.util.Log
import com.heypudu.heypudu.network.NewsApiService
import com.heypudu.heypudu.network.NewsArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository {

    private val newsApiService = com.heypudu.heypudu.network.RetrofitClient.createService<NewsApiService>()

    // Reemplaza con tu API key de NewsAPI
    private val API_KEY = "0417025488a0483882b50925a1d581a2"

    suspend fun getMusicalNewsDefault(): Result<List<NewsArticle>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = newsApiService.searchMusicalEvents(
                query = "música conciertos eventos en vivo",
                apiKey = API_KEY
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d("NewsRepository", "Default musical news found: ${response.body()!!.articles.size}")
                Result.success(response.body()!!.articles)
            } else {
                Log.e("NewsRepository", "Error: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun searchMusicalEvents(query: String): Result<List<NewsArticle>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = newsApiService.searchMusicalEvents(
                query = query,
                apiKey = API_KEY
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d("NewsRepository", "Articles found: ${response.body()!!.articles.size}")
                Result.success(response.body()!!.articles)
            } else {
                Log.e("NewsRepository", "Error: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun searchMusicalArtists(artistName: String): Result<List<NewsArticle>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val query = "artista $artistName música"
            val response = newsApiService.searchMusicalEvents(
                query = query,
                apiKey = API_KEY
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.articles)
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchConcerts(): Result<List<NewsArticle>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = newsApiService.searchMusicalEvents(
                query = "conciertos eventos musicales",
                apiKey = API_KEY
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.articles)
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

