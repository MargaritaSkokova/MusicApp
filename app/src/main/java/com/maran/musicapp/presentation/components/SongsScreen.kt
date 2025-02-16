@file:OptIn(ExperimentalMaterial3Api::class)

package com.maran.musicapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.maran.musicapp.R
import com.maran.musicapp.domain.TimeConvertor
import com.maran.musicapp.domain.models.Song
import kotlin.reflect.KFunction2

@Composable
fun Search(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.search_input)) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("").also { onClear() } }) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() })
    )
}

@Composable
fun SongElement(
    modifier: Modifier = Modifier,
    song: Song,
    chosen: MutableLongState,
    choseSongCallback: KFunction2<Song, Int, Unit>,
    index: Int
) {
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .height(80.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(enabled = true) {
                chosen.longValue = song.id
                choseSongCallback(song, index)
            },
        verticalAlignment = Alignment.CenterVertically,


        ) {
        when (song) {
            is Song.LocalSong -> {
                if (song.cover == null) {
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .size(60.dp),
                        painter = painterResource(id = R.drawable.song_default),
                        contentDescription = ""
                    )
                } else {
                    song.cover?.asImageBitmap()?.let {
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
                if (song.coverUrl == null) {
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
                        model = song.coverUrl,
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
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.surfaceTint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            modifier = Modifier.padding(10.dp),
            text = TimeConvertor.formatMilliseconds(song.duration),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun SongList(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    chosen: MutableLongState,
    state: LazyListState,
    choseSongCallback: KFunction2<Song, Int, Unit>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(8.dp),
        state = state
    ) {
        itemsIndexed(songs) { index, song ->
            SongElement(
                song = song,
                choseSongCallback = choseSongCallback,
                index = index,
                chosen = chosen
            )
        }
    }
}