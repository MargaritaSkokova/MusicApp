package com.maran.musicapp.presentation.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.maran.musicapp.presentation.components.MusicViewModel
import com.maran.musicapp.R
import com.maran.musicapp.domain.TimeConvertor
import com.maran.musicapp.domain.models.Song
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    song: MutableState<Song>,
    show: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            IconButton(onClick = { show.value = false }) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        when (song.value) {
            is Song.LocalSong -> {
                if ((song.value as Song.LocalSong).cover == null) {
                    Image(
                        painter = painterResource(R.drawable.song_default),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(16.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    (song.value as Song.LocalSong).cover?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(16.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }

            is Song.OnlineSong -> {
                if ((song.value as Song.OnlineSong).coverUrl == null) {
                    Image(
                        painter = painterResource(R.drawable.song_default),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(16.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    AsyncImage(
                        model = (song.value as Song.OnlineSong).coverUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 8.dp)
                .padding(end = 8.dp)
                .background(MaterialTheme.colorScheme.secondary)
        )
        Player(
            Modifier.padding(bottom = 20.dp),
            song
        )
    }
}


@Composable
fun Player(
    modifier: Modifier = Modifier,
    song: MutableState<Song>
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(36.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ArtistPanel(
            Modifier
                .padding(top = 24.dp)
                .padding(start = 16.dp),
            song.value.artist,
            song.value.title,
            song.value.album
        )
        PlayerPanel(Modifier,song.value.duration)
        PlayButtons(Modifier.padding(bottom = 24.dp))
    }
}

@Composable
fun ArtistPanel(
    modifier: Modifier = Modifier,
    artistName: String,
    songName: String,
    albumName: String?
) {
    Column(modifier = modifier) {
        Text(
            text = songName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .basicMarquee()
        )
        if (albumName != null) Text(
            text = albumName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = artistName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.surfaceTint
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerPanel(
    modifier: Modifier = Modifier,
    duration: Int,
    musicViewModel: MusicViewModel = hiltViewModel()
) {
    var sliderPosition by remember { mutableStateOf(0f) }

    LaunchedEffect(musicViewModel.exoPlayer) {
        while (true) {
            sliderPosition = musicViewModel.exoPlayer.currentPosition.toFloat()
            delay(100)
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Slider(
            value = sliderPosition,
            onValueChange = { newValue ->
                sliderPosition = newValue
            },
            onValueChangeFinished = {
                musicViewModel.exoPlayer.seekTo(sliderPosition.toLong())
            },
            valueRange = 0f..duration.toFloat(),
            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.onSecondaryContainer),
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            track = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(MaterialTheme.colorScheme.onSecondaryContainer)
                )
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                TimeConvertor.formatMilliseconds(sliderPosition.toInt()),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                TimeConvertor.formatMilliseconds(duration),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
        }

    }
}

@Composable
fun PlayButtons(
    modifier: Modifier = Modifier,
    musicViewModel: MusicViewModel = hiltViewModel()
) {
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { musicViewModel.getPrev() }) {
            Icon(
                Icons.Default.SkipPrevious,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        IconButton(onClick = { musicViewModel.pauseOrPlay() }) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.fillMaxSize()
            )
        }

        IconButton(onClick = { musicViewModel.getNext() }) {
            Icon(
                Icons.Default.SkipNext,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun PlayerPeek(
    modifier: Modifier = Modifier,
    song: MutableState<Song>,
    show: MutableState<Boolean>,
    musicViewModel: MusicViewModel = hiltViewModel()
) {
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .height(80.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { show.value = true },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (song.value) {
            is Song.LocalSong -> {
                if ((song.value as Song.LocalSong).cover == null) {
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .size(60.dp),
                        painter = painterResource(id = R.drawable.song_default),
                        contentDescription = ""
                    )
                } else {
                    (song.value as Song.LocalSong).cover?.asImageBitmap()?.let {
                        Image(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .size(60.dp),
                            bitmap = it,
                            contentDescription = ""
                        )
                    }
                }
            }

            is Song.OnlineSong -> {
                if ((song.value as Song.OnlineSong).coverUrl == null) {
                    Image(
                        painter = painterResource(R.drawable.song_default),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .size(60.dp)
                    )
                } else {
                    AsyncImage(
                        model = (song.value as Song.OnlineSong).coverUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .size(60.dp)
                    )
                }
            }
        }


        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.value.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.value.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.surfaceTint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = { musicViewModel.pauseOrPlay() }) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
        }
    }
}