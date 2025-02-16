package com.maran.musicapp.data.online

import android.util.Log
import com.maran.musicapp.data.ISongRepository
import com.maran.musicapp.domain.models.Song
import com.maran.musicapp.data.online.network.RetrofitClient
import com.maran.musicapp.data.online.network.SongApi
import com.maran.musicapp.data.online.network.models.songModelMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class OnlineSongRepository : ISongRepository {
    private val retrofitClient = RetrofitClient.retrofitClient()
    private val songApi = retrofitClient.create(SongApi::class.java)

    override fun getAllSongs(): Flow<Song> = flow {
        songApi.getAllSongs()
            .onFailure { Log.v("ApiError", it.message ?: "") }
            .onSuccess {
                it.tracks.data.forEach { song ->
                    Log.v("song", song.toString()); emit(
                    songModelMapper(song)
                )
                }
            }
    }.flowOn(Dispatchers.IO)

    override fun findSong(query: String): Flow<Song> = flow {
        songApi.getByQuery(query)
            .onFailure { Log.v("ApiError", it.message ?: "") }
            .onSuccess {
                it.data.forEach { song -> emit(songModelMapper(song)) }
            }
    }.flowOn(Dispatchers.IO)
}