package com.maran.musicapp.data.online.network.models

import com.maran.musicapp.domain.models.Song

fun songModelMapper(songModel: SongModel): Song.OnlineSong {
    return Song.OnlineSong(
        songModel.id, songModel.title, songModel.artist.name,
        songModel.album.title, 30000, songModel.preview, songModel.album.cover_xl
    )
}