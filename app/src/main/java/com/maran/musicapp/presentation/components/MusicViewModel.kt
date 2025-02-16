package com.maran.musicapp.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.maran.musicapp.MusicService
import com.maran.musicapp.domain.models.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    val exoPlayer: ExoPlayer,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val localSongs = MutableStateFlow<List<Song>>(emptyList())
    val onlineSongs = MutableStateFlow<List<Song>>(emptyList())
    val currSong = mutableStateOf<Song?>(null)
    val currSongId = mutableLongStateOf(-1)
    val currSongIndex = mutableIntStateOf(-1)
    val currPlaylist = mutableStateOf<List<Song>>(emptyList())
    val wasPlayed = mutableStateOf(false)
    val showPlayer = mutableStateOf(false)
    val duration = mutableIntStateOf(0)
    var playerState by mutableStateOf(Player.STATE_IDLE)
    private val _isPlaying = MutableStateFlow(exoPlayer.isPlaying)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    var wasLoadedLocal = false
    var wasLoadedOnline = false

    init {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                playerState = state
                if (state == Player.STATE_ENDED) {
                    getNext()
                }
            }
        }

        exoPlayer.addListener(listener)
    }

    fun startMusicService() {
        val intent = Intent(context, MusicService::class.java)
        context.startForegroundService(intent)
    }

    fun updateSong(song: Song, index: Int, playlist: List<Song>) {
        currSong.value = song
        currSongId.longValue = song.id
        currSongIndex.intValue = index
        currPlaylist.value = playlist
        duration.intValue = song.duration
        showPlayer.value = true
        if (!wasPlayed.value) startMusicService()
        playSong()
        wasPlayed.value = true
    }

    fun playSong() {
        val uri = when (currSong.value) {
            null -> return
            is Song.LocalSong -> (currSong.value as Song.LocalSong).uri
            is Song.OnlineSong -> Uri.parse((currSong.value as Song.OnlineSong).streamUrl)
        }

        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun pauseOrPlay() {
        if (isPlaying.value) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun getNext() {
        currSongIndex.intValue = (currSongIndex.intValue + 1) % currPlaylist.value.size
        currSong.value = currPlaylist.value[currSongIndex.intValue]
        currSongId.longValue = currSong.value?.id ?: -1

        if (isPlaying.value) {
            playSong()
        } else {
            playSong()
            exoPlayer.pause()
        }
    }

    fun getPrev() {
        currSongIndex.intValue =
            if (currSongIndex.intValue - 1 < 0) currPlaylist.value.size - 1 else currSongIndex.intValue - 1
        currSong.value = currPlaylist.value[currSongIndex.intValue]
        currSongId.longValue = currSong.value?.id ?: -1

        if (isPlaying.value) {
            playSong()
        } else {
            playSong()
            exoPlayer.pause()
        }
    }

    override fun onCleared() {
        coroutineScope.cancel()
        exoPlayer.release()
    }
}