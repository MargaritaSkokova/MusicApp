package com.maran.musicapp.data.online.network.models

import kotlinx.serialization.Serializable

@Serializable
data class AlbumModel(
    val id: Long,
    val title: String,
    val cover_xl: String?
)
