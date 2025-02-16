package com.maran.musicapp.data.online.network

import com.maran.musicapp.data.online.network.models.ResponseModel
import com.maran.musicapp.data.online.network.models.SongModel
import com.maran.musicapp.data.online.network.models.TracksModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SongApi {
    @GET("chart")
    suspend fun getAllSongs(): Result<ResponseModel>

    @GET("track/{id}")
    suspend fun getSongById(@Path("id") id: Long): Result<SongModel?>

    @GET("search")
    suspend fun getByQuery(@Query("q") query: String): Result<TracksModel>
}