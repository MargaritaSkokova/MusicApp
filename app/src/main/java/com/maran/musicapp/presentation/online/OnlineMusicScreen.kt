package com.maran.musicapp.presentation.online

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
fun OnlineMusicScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onlineViewModel: OnlineViewModel = hiltViewModel()
) {
    val permission = remember { mutableStateOf(false) }
    RequestPermission { permission.value = true }
    if (permission.value) {
        val songs by onlineViewModel.songs.collectAsState()
        val foundSongs by onlineViewModel.foundSongs.collectAsState()
        var searchQuery by remember { mutableStateOf("") }

        val coroutineScope = rememberCoroutineScope()
        var backPressHandled by remember { mutableStateOf(false) }
        BackHandler(enabled = !backPressHandled) {
            if (onlineViewModel.showFound.value) {
                backPressHandled = true
                coroutineScope.launch {
                    awaitFrame()
                    onlineViewModel.showFound.value = false
                    searchQuery = ""
                    backPressHandled = false
                }
            }
        }

        Column {
            Search(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { onlineViewModel.findSongs(searchQuery) },
                onClear = { onlineViewModel.backToAllSongs() }
            )
            AnimatedContent(
                targetState = onlineViewModel.showFound,
                label = "animated content"
            ) { targetStatus ->
                when (targetStatus.value) {
                    false -> SongList(
                        songs = songs,
                        chosen = onlineViewModel.chosen,
                        state = onlineViewModel.listState,
                        choseSongCallback = onlineViewModel::chooseSong
                    )

                    true -> SongList(
                        songs = foundSongs,
                        chosen = onlineViewModel.chosen,
                        state = onlineViewModel.listState,
                        choseSongCallback = onlineViewModel::chooseSong
                    )
                }
            }
        }
    }
}