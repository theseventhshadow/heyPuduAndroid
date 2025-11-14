package com.heypudu.heypudu.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    fun play(context: Context, audioId: String, audioUrl: String, onCompletionCallback: (() -> Unit)? = null) {
        try {
            if (currentAudioId == audioId && mediaPlayer != null) {
                // Si está pausado, solo reanudar
                if (wasPaused) {
                    mediaPlayer?.start()
                    wasPaused = false
                    _state.value = AudioPlayerState(audioId = audioId, isPlaying = true, positionMs = lastPosition, durationMs = mediaPlayer?.duration ?: 0)
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
                }
                return
            }
            // Si cambia de audio, liberar y crear nuevo MediaPlayer
            stop()
            currentAudioId = audioId
            lastAudioUrl = audioUrl
            onCompletion = onCompletionCallback
            wasPaused = false
            lastPosition = 0
            mediaPlayer = MediaPlayer().apply {
                if (audioUrl.startsWith("http")) {
                    setDataSource(audioUrl)
                } else {
                    setDataSource(context, Uri.parse(audioUrl))
                }
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
                prepareAsync()
                setOnPreparedListener {
                    start()
                    _state.value = AudioPlayerState(audioId = audioId, isPlaying = true, positionMs = 0, durationMs = duration)
                    CoroutineScope(Dispatchers.Main).launch {
                        while (true) {
                            try {
                                val playing = isPlaying
                                _state.value = AudioPlayerState(
                                    audioId = audioId,
                                    isPlaying = playing,
                                    positionMs = currentPosition,
                                    durationMs = duration
                                )
                                if (!playing) break
                            } catch (e: IllegalStateException) {
                                Log.e("AudioPlayerController", "Error isPlaying: ${e.message}")
                                break
                            }
                            delay(250)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerController", "Error play: ${e.message}")
            stop()
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

    fun stop() {
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
        lastPosition = 0
        wasPaused = false
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
