package com.heypudu.heypudu.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UploadProgress(
    val isUploading: Boolean = false,
    val progress: Float = 0f,
    val message: String = "",
    val isCompleted: Boolean = false
)

object UploadStateManager {
    private val _uploadState = MutableStateFlow(UploadProgress())
    val uploadState: StateFlow<UploadProgress> = _uploadState

    fun setUploading(isUploading: Boolean) {
        _uploadState.value = _uploadState.value.copy(isUploading = isUploading, progress = if (isUploading) 0f else 0f)
    }

    fun updateProgress(progress: Float, message: String = "") {
        _uploadState.value = _uploadState.value.copy(progress = progress, message = message)
    }

    fun setCompleted() {
        _uploadState.value = _uploadState.value.copy(isUploading = false, isCompleted = true, progress = 100f, message = "Publicado!")
    }

    fun resetState() {
        _uploadState.value = UploadProgress()
    }
}

