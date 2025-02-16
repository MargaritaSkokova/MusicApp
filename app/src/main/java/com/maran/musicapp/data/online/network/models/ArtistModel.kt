package com.maran.musicapp.data.online.network.models

import kotlinx.serialization.Serializable

@Serializable
data class ArtistModel(
    val id: Long,
    val name: String)
