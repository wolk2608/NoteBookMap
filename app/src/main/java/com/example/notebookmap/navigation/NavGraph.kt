package com.example.notebookmap.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.notebookmap.navigation.paths.mapNav
import com.example.notebookmap.navigation.paths.noteDescriptionNav
import com.example.notebookmap.navigation.paths.noteListNav
import com.example.notebookmap.presentation.components.BottomNav
import com.example.notebookmap.utils.Constants.SCREEN_TRANSIT_DURATION_MILLIS
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SetupNavGraph() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val navController = rememberAnimatedNavController()
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(primaryColor, darkIcons = false)
    systemUiController.setNavigationBarColor(primaryColor, darkIcons = false)

    Scaffold(
        topBar = {},
        bottomBar = { BottomNav(navController = navController) },
        content = { padding ->
            AnimatedNavHost(
                modifier = Modifier.padding(padding),
                navController = navController,
                startDestination = Screen.Map.route
            ) {
                noteListNav(navController)
                mapNav(navController)
                noteDescriptionNav(navController)
            }
        }
    )
}

val defaultEnter = fadeIn(animationSpec = tween(SCREEN_TRANSIT_DURATION_MILLIS))
val defaultExit = fadeOut(animationSpec = tween(SCREEN_TRANSIT_DURATION_MILLIS))