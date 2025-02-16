package com.maran.musicapp.presentation.local

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.maran.musicapp.domain.RequestPermission
import com.maran.musicapp.presentation.components.Search
import com.maran.musicapp.presentation.components.SongList
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

@Composable
fun LocalMusicScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    localViewModel: LocalViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val permission = remember { mutableStateOf(false) }
    RequestPermission { permission.value = true }
    if (permission.value) {
        val songs by localViewModel.songs.collectAsState()
        val foundSongs by localViewModel.foundSongs.collectAsState()
        var searchQuery by remember { mutableStateOf("") }

        var backPressHandled by remember { mutableStateOf(false) }
        BackHandler(enabled = !backPressHandled) {
            if (localViewModel.showFound.value) {
                backPressHandled = true
                coroutineScope.launch {
                    awaitFrame()
                    localViewModel.showFound.value = false
                    searchQuery = ""
                    backPressHandled = false
                }
            }
        }

        Column {
            Search(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { localViewModel.findSongs(searchQuery) },
                onClear = { localViewModel.backToAllSongs() }
            )
            AnimatedContent(
                targetState = localViewModel.showFound,
                label = "animated content"
            ) { targetStatus ->
                when (targetStatus.value) {
                    false -> SongList(
                        songs = songs,
                        chosen = localViewModel.chosen,
                        state = localViewModel.listState,
                        choseSongCallback = localViewModel::chooseSong
                    )

                    true -> SongList(
                        songs = foundSongs,
                        chosen = localViewModel.chosen,
                        state = localViewModel.listState,
                        choseSongCallback = localViewModel::chooseSong
                    )
                }
            }
        }
    }
}