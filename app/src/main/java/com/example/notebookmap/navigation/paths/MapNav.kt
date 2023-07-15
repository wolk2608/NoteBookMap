package com.example.notebookmap.navigation.paths

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.notebookmap.navigation.Screen
import com.example.notebookmap.navigation.defaultEnter
import com.example.notebookmap.navigation.defaultExit
import com.example.notebookmap.presentation.screens.map.MapContract
import com.example.notebookmap.presentation.screens.map.MapScreen
import com.example.notebookmap.presentation.screens.map.MapViewModel
import com.google.accompanist.navigation.animation.composable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mapNav(navController: NavController) {
    composable(
        route = Screen.Map.route,
        enterTransition = { defaultEnter },
        exitTransition = { defaultExit }
    ) {
        val viewModel = koinViewModel<MapViewModel>()
        MapScreen(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = viewModel::setEvent,
            onNavigationRequested = { navigationEffect ->
                navController.navigate(
                    when (navigationEffect) {
                        is MapContract.Effect.Navigation.ToNoteDescription -> Screen.NoteDescription.passParam(navigationEffect.noteId)
                    }
                )
            }
        )
    }
}