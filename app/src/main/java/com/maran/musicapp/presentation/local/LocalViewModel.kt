package com.maran.musicapp.presentation.local

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.maran.musicapp.presentation.components.MusicViewModel
import com.maran.musicapp.data.local.LocalSongRepository
import com.maran.musicapp.domain.models.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    private val repository: LocalSongRepository,
    private val musicViewModel: MusicViewModel
) :
    ViewModel() {
    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val showFound = mutableStateOf(false)

    private val _songs = musicViewModel.localSongs
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()
    val chosen = mutableLongStateOf(-1)
    val listState = LazyListState()

    private val _foundSongs = MutableStateFlow<List<Song>>(emptyList())
    val foundSongs: StateFlow<List<Song>> = _foundSongs.asStateFlow()

    init {
        if (!musicViewModel.wasLoadedLocal) {
            getAllSongs()
            musicViewModel.wasLoadedLocal = true
        }
    }

    private fun getAllSongs() {
        coroutineScope.launch {
            repository.getAllSongs()
                .flowOn(Dispatchers.IO)
                .collect { song ->
                    _songs.value += song
                }
        }
    }

    fun findSongs(query: String) {
        coroutineScope.launch {
            _foundSongs.value = emptyList()
            if (query.isNotEmpty()) {
                showFound.value = true
                repository.findSong(query)
                    .flowOn(Dispatchers.IO)
                    .collect { song ->
                        _foundSongs.value += song
                    }
            } else {
                backToAllSongs()
            }
        }
    }

    fun backToAllSongs() {
        showFound.value = false
        scrollToItem()
    }

    private fun scrollToItem() {
        coroutineScope.launch {
            if (chosen.longValue != -1L) {
                listState.scrollToItem(findIndexById())
            }
        }
    }

    private fun findIndexById(): Int {
        return songs.value.indexOf(songs.value.find { it.id == chosen.longValue })
    }

    fun chooseSong(song: Song, index: Int) {
        if (showFound.value) {
            musicViewModel.updateSong(song, index, foundSongs.value)
        } else {
            musicViewModel.updateSong(song, index, songs.value)
        }
    }
}