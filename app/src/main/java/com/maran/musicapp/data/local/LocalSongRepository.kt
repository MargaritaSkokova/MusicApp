package com.maran.musicapp.data.local

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.maran.musicapp.data.ISongRepository
import com.maran.musicapp.domain.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocalSongRepository @Inject constructor(private val contentResolver: ContentResolver) :
    ISongRepository {
    override fun getAllSongs(): Flow<Song> = flow {
        val songs = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val artist = cursor.getString(artistColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val album = getAlbumName(albumId)
                val duration = cursor.getInt(durationColumn)

                val contentUri = ContentUris.withAppendedId(uri, id)
                val cover = getAlbumArtwork(albumId) ?: getSongArtwork(contentUri)

                emit(Song.LocalSong(id, name, artist, album, contentUri, cover, duration))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun findSong(query: String): Flow<Song> = flow {
        val songs = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")

        contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val artist = cursor.getString(artistColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val album = getAlbumName(albumId)
                val duration = cursor.getInt(durationColumn)

                val contentUri = ContentUris.withAppendedId(uri, id)
                val cover = getAlbumArtwork(albumId) ?: getSongArtwork(contentUri)

                emit(Song.LocalSong(id, name, artist, album, contentUri, cover, duration))
            }
        }
    }.flowOn(Dispatchers.IO)


    private fun getAlbumArtwork(albumId: Long): Bitmap? {
        val albumArtUri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )

        return try {
            contentResolver.openInputStream(albumArtUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getSongArtwork(songUri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            contentResolver.openFileDescriptor(songUri, "r")?.use { fd ->
                retriever.setDataSource(fd.fileDescriptor)
                val art = retriever.embeddedPicture
                if (art != null) BitmapFactory.decodeByteArray(art, 0, art.size) else null
            }
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }

    private fun getAlbumName(albumId: Long): String? {
        val albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Albums.ALBUM)
        val selection = "${MediaStore.Audio.Albums._ID} = ?"
        val selectionArgs = arrayOf(albumId.toString())

        return contentResolver.query(albumUri, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
                    cursor.getString(albumColumn)
                } else {
                    null
                }
            }
    }
}