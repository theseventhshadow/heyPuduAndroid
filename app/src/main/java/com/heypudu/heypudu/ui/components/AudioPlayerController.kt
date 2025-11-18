package com.heypudu.heypudu.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Estado de reproducción observable
data class AudioPlayerState(
    val audioId: String? = null,
    val isPlaying: Boolean = false,
    val positionMs: Int = 0,
    val durationMs: Int = 0
)

object AudioPlayerController {
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudioId: String? = null
    private var onCompletion: (() -> Unit)? = null
    private val _state = MutableStateFlow(AudioPlayerState())
    val state: StateFlow<AudioPlayerState> = _state.asStateFlow()
    private var lastPosition: Int = 0
    private var wasPaused: Boolean = false
    private var lastAudioUrl: String? = null

    private fun getLocalAudioFile(context: Context, audioId: String): File {
        val cacheDir = context.cacheDir
        return File(cacheDir, "audio_$audioId.m4a")
    }

    private suspend fun downloadAudioFile(context: Context, audioUrl: String, audioId: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val file = getLocalAudioFile(context, audioId)
                if (!file.exists()) {
                    val url = URL(audioUrl)
                    url.openStream().use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                file
            } catch (e: Exception) {
                Log.e("AudioPlayerController", "Error descargando audio: ${e.message}")
                null
            }
        }
    }

    fun play(context: Context, audioId: String, audioUrl: String, onCompletionCallback: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            val localFile = if (audioUrl.startsWith("http")) {
                downloadAudioFile(context, audioUrl, audioId)
            } else {
                File(audioUrl)
            }
            if (localFile == null || !localFile.exists()) {
                Log.e("AudioPlayerController", "No se pudo obtener el archivo de audio local")
                return@launch
            }
            try {
                // Si se va a retomar el mismo audio, no resetear la posición
                val isSameAudio = currentAudioId == audioId && wasPaused && lastPosition > 0
                stop(resetPosition = !isSameAudio)
                currentAudioId = audioId
                lastAudioUrl = localFile.absolutePath
                onCompletion = onCompletionCallback
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(localFile.absolutePath)
                    setOnCompletionListener {
                        onCompletion?.invoke()
                        currentAudioId = null
                        lastPosition = 0
                        wasPaused = false
                        _state.value = AudioPlayerState(audioId = null, isPlaying = false, positionMs = 0, durationMs = duration)
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("AudioPlayerController", "Error: what=$what, extra=$extra")
                        currentAudioId = null
                        lastPosition = 0
                        wasPaused = false
                        _state.value = AudioPlayerState(audioId = null, isPlaying = false, positionMs = 0, durationMs = 0)
                        false
                    }
                    prepare()
                    if (isSameAudio) {
                        seekTo(lastPosition)
                    }
                    start()
                    _state.value = AudioPlayerState(audioId = audioId, isPlaying = true, positionMs = if (isSameAudio) lastPosition else 0, durationMs = duration)
                }
                wasPaused = false
                CoroutineScope(Dispatchers.Main).launch {
                    while (mediaPlayer?.isPlaying == true) {
                        _state.value = AudioPlayerState(
                            audioId = audioId,
                            isPlaying = true,
                            positionMs = mediaPlayer?.currentPosition ?: 0,
                            durationMs = mediaPlayer?.duration ?: 0
                        )
                        delay(250)
                    }
                }
            } catch (e: Exception) {
                Log.e("AudioPlayerController", "Error al reproducir audio: ${e.message}")
            }
        }
    }

    fun pause() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.pause()
                lastPosition = it.currentPosition
                wasPaused = true
                _state.value = AudioPlayerState(
                    audioId = currentAudioId,
                    isPlaying = false,
                    positionMs = lastPosition,
                    durationMs = it.duration
                )
            } catch (e: Exception) {
                Log.e("AudioPlayerController", "Error pause: ${e.message}")
            }
        }
    }

    fun stop(resetPosition: Boolean = true) {
        try {
            mediaPlayer?.let {
                try {
                    it.stop()
                } catch (e: Exception) {
                    Log.e("AudioPlayerController", "Error stop: ${e.message}")
                }
                try {
                    it.release()
                } catch (e: Exception) {
                    Log.e("AudioPlayerController", "Error release: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerController", "Error stop outer: ${e.message}")
        }
        mediaPlayer = null
        currentAudioId = null
        if (resetPosition) {
            lastPosition = 0
            wasPaused = false
        }
        lastAudioUrl = null
        _state.value = AudioPlayerState()
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let {
            try {
                it.seekTo(positionMs)
                lastPosition = positionMs
                _state.value = AudioPlayerState(
                    audioId = currentAudioId,
                    isPlaying = it.isPlaying,
                    positionMs = positionMs,
                    durationMs = it.duration
                )
            } catch (e: Exception) {
                Log.e("AudioPlayerController", "Error seekTo: ${e.message}")
            }
        }
    }

    fun isPlaying(audioId: String): Boolean {
        return _state.value.audioId == audioId && _state.value.isPlaying
    }

    fun getCurrentPosition(): Int {
        return _state.value.positionMs
    }

    fun getDuration(): Int {
        return _state.value.durationMs
    }

    fun observeState(): StateFlow<AudioPlayerState> = state
}
