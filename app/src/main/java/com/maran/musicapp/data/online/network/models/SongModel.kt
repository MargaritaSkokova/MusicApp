package com.maran.musicapp.data.online.network.models

import kotlinx.serialization.Serializable

@Serializable
data class SongModel(
    val id: Long,
    val title: String,
    val duration: Int,
    val preview: String,
    val artist: ArtistModel,
    val album: AlbumModel
)
