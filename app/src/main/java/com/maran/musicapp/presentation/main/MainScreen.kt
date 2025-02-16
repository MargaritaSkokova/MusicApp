package com.maran.musicapp.presentation.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.maran.musicapp.presentation.components.MusicViewModel
import com.maran.musicapp.domain.models.Song
import com.maran.musicapp.presentation.navigation.NavigationStack
import com.maran.musicapp.presentation.navigation.Screen
import com.maran.musicapp.presentation.player.PlayerPeek
import com.maran.musicapp.presentation.player.PlayerScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    musicViewModel: MusicViewModel = hiltViewModel(),
    showNavigationBar: MutableState<Boolean>
) {
    AnimatedContent(
        targetState = musicViewModel.showPlayer.value,
        label = "animated content"
    ) { targetValue ->
        when (targetValue) {
            true -> {
                showNavigationBar.value = false
                PlayerScreen(
                    Modifier,
                    musicViewModel.currSong as MutableState<Song>,
                    show = musicViewModel.showPlayer
                )
            }

            false -> {
                showNavigationBar.value = true
                if (!musicViewModel.showPlayer.value) {
                    NavigationStack(navController, modifier)
                }
                if (musicViewModel.wasPlayed.value) {
                    Column(
                        modifier = modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        PlayerPeek(
                            modifier = Modifier,
                            musicViewModel.currSong as MutableState<Song>,
                            show = musicViewModel.showPlayer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MusicBottomNavigation(navController: NavController) {
    val items = listOf(Screen.LocalMusic, Screen.OnlineMusic)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = { navController.navigate(screen.route) },
                icon = {
                    if (screen.icon != null && screen.labelId != null) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = stringResource(screen.labelId),
                            tint = if (currentRoute == screen.route) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                label = {
                    if (screen.labelId != null) {
                        Text(
                            text = stringResource(screen.labelId),
                            style = MaterialTheme.typography.titleSmall,
                            color = if (currentRoute == screen.route) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            )
        }
    }
}
