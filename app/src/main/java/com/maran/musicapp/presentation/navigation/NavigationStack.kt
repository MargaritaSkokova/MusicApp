package com.maran.musicapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.maran.musicapp.presentation.local.LocalMusicScreen
import com.maran.musicapp.presentation.online.OnlineMusicScreen

@Composable
fun NavigationStack(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.LocalMusic.route
    ) {
        composable(Screen.LocalMusic.route) { LocalMusicScreen(navController = navController) }
        composable(Screen.OnlineMusic.route) { OnlineMusicScreen(navController = navController) }
    }
}