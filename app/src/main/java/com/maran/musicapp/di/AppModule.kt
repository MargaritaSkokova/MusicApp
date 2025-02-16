package com.maran.musicapp.di

import android.content.ContentResolver
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import com.maran.musicapp.presentation.components.MusicViewModel
import com.maran.musicapp.data.local.LocalSongRepository
import com.maran.musicapp.data.online.OnlineSongRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(value = [SingletonComponent::class])
object ContentResolverModule {
    @Provides
    @Singleton
    fun contentResolverProvider(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }
}

@Module
@InstallIn(value = [SingletonComponent::class])
object LocalRepositoryProvider {
    @Provides
    @Singleton
    fun localRepository(contentResolver: ContentResolver): LocalSongRepository {
        return LocalSongRepository(contentResolver)
    }
}

@Module
@InstallIn(value = [SingletonComponent::class])
object OnlineRepositoryProvider {
    @Provides
    @Singleton
    fun onlineRepository(): OnlineSongRepository {
        return OnlineSongRepository()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ExoPlayerModule {
    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun exoPlayerProvider(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).setLoadControl(
            DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                )
                .build()
        ).build()
    }
}

@Module
@InstallIn(value = [SingletonComponent::class])
object MusicViewModelProvider {
    @Provides
    @Singleton
    fun musicViewModel(exoPlayer: ExoPlayer, @ApplicationContext context: Context): MusicViewModel {
        return MusicViewModel(exoPlayer, context)
    }
}