package com.maran.musicapp.data

import com.maran.musicapp.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface ISongRepository {
    fun getAllSongs(): Flow<Song>
    fun findSong(query: String): Flow<Song>
}