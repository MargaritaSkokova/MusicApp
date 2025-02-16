package com.maran.musicapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.maran.musicapp.R

sealed class Screen(val route: String, val labelId: Int?, val icon: ImageVector?) {
    object LocalMusic : Screen("loaded_music", R.string.local_music, Icons.Default.LibraryMusic)
    object OnlineMusic : Screen("external_music", R.string.online_music, Icons.Default.QueueMusic)
    object PlayerScreen : Screen("player_screen", null, null)
}