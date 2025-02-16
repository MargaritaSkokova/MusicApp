package com.maran.musicapp.domain.models

import android.graphics.Bitmap
import android.net.Uri

sealed class Song {
    abstract val id: Long
    abstract val title: String
    abstract val artist: String
    abstract val album: String?
    abstract val duration: Int

    class LocalSong(
        override val id: Long,
        override val title: String,
        override val artist: String,
        override val album: String?,
        val uri: Uri,
        val cover: Bitmap?,
        override val duration: Int,
    ) : Song()

    class OnlineSong(
        override val id: Long,
        override val title: String,
        override val artist: String,
        override val album: String?,
        override val duration: Int,
        val streamUrl: String,
        val coverUrl: String?
    ) : Song()
}


