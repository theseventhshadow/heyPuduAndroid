package com.heypudu.heypudu.features.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heypudu.heypudu.data.NewsRepository
import com.heypudu.heypudu.network.NewsArticle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val newsRepository = NewsRepository()

    private val _musicalEvents = MutableStateFlow<List<NewsArticle>>(emptyList())
    val musicalEvents: StateFlow<List<NewsArticle>> = _musicalEvents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        // Cargar noticias musicales por defecto al inicializar
        loadDefaultMusicalNews()
    }

    fun loadDefaultMusicalNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = newsRepository.getMusicalNewsDefault()
            result.onSuccess { articles ->
                _musicalEvents.value = articles
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message
                _isLoading.value = false
            }
        }
    }

    fun searchMusicalEvents(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = newsRepository.searchMusicalEvents(query)
            result.onSuccess { articles ->
                _musicalEvents.value = articles
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message
                _isLoading.value = false
            }
        }
    }

    fun searchMusicalArtists(artistName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = newsRepository.searchMusicalArtists(artistName)
            result.onSuccess { articles ->
                _musicalEvents.value = articles
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message
                _isLoading.value = false
            }
        }
    }

    fun searchConcerts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = newsRepository.searchConcerts()
            result.onSuccess { articles ->
                _musicalEvents.value = articles
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message
                _isLoading.value = false
            }
        }
    }
}

