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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.notebookmap.presentation.components.BottomNav
import com.example.notebookmap.presentation.screens.map.MapContract
import com.example.notebookmap.presentation.screens.map.MapScreen
import com.example.notebookmap.presentation.screens.map.MapViewModel
import com.example.notebookmap.presentation.screens.note_description.NoteDescriptionContract
import com.example.notebookmap.presentation.screens.note_description.NoteDescriptionScreen
import com.example.notebookmap.presentation.screens.note_description.NoteDescriptionViewModel
import com.example.notebookmap.presentation.screens.notes_list.NotesListContract
import com.example.notebookmap.presentation.screens.notes_list.NotesListScreen
import com.example.notebookmap.presentation.screens.notes_list.NotesListViewModel
import com.example.notebookmap.utils.Constants
import com.example.notebookmap.utils.Constants.SCREEN_TRANSIT_DURATION_MILLIS
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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
                startDestination = Screen.Map.route) {
                composable(
                    route = Screen.NotesList.route,
                    enterTransition = { fadeIn(animationSpec = tween(SCREEN_TRANSIT_DURATION_MILLIS)) },
                    exitTransition = { fadeOut(animationSpec = tween(SCREEN_TRANSIT_DURATION_MILLIS)) }
                ) {
                    val viewModel = koinViewModel<NotesListViewModel>()
                    val state = viewModel.viewState.value
                    NotesListScreen(
                        state = state,
                        effectFlow = viewModel.effect,
                        onEventSent = viewModel::setEvent,
                        onNavigationRequested = { navigationEffect ->
                            navController.navigate(
                                when (navigationEffect) {
                                    is NotesListContract.Effect.Navigation.ToNoteDescription -> Screen.NoteDescription.passParam(navigationEffect.noteId)
                                    //is NotesListContract.Effect.Navigation.ToNoteDescription -> Screen.NoteDescription.route
                                }
                            )
                        }
                    )
                }
                composable(
                    route = Screen.Map.route,
                    enterTransition = { fadeIn(animationSpec = tween(SCREEN_TRANSIT_DURATION_MILLIS)) },
                    exitTransition = { fadeOut(animationSpec = tween(SCREEN_TRANSIT_DURATION_MILLIS)) }
                ) {
                    val viewModel = koinViewModel<MapViewModel>()
                    val state = viewModel.viewState.value
                    MapScreen(
                        state = state,
                        effectFlow = viewModel.effect,
                        onEventSent = viewModel::setEvent,
                        onNavigationRequested = { navigationEffect ->
                            navController.navigate(
                                when (navigationEffect) {
                                    is MapContract.Effect.Navigation.ToNoteDescription -> Screen.NoteDescription.passParam(navigationEffect.noteId)
                                    //is MapContract.Effect.Navigation.ToNoteDescription -> Screen.NoteDescription.route
                                }
                            )
                        }
                    )
                }
                composable(
                    route = Screen.NoteDescription.route,
                    enterTransition = { fadeIn(animationSpec = tween(SCREEN_TRANSIT_DURATION_MILLIS)) },
                    exitTransition = { fadeOut(animationSpec = tween(SCREEN_TRANSIT_DURATION_MILLIS)) },
                    arguments = listOf(navArgument(Constants.DESCRIPTION_ARGUMENT) { type = NavType.LongType })
                ) {
                    val noteId = it.arguments?.getLong(Constants.DESCRIPTION_ARGUMENT) ?: -1L
                    val viewModel = koinViewModel<NoteDescriptionViewModel> { parametersOf(noteId) }
                    NoteDescriptionScreen(
                        state = viewModel.viewState.value,
                        effectFlow = viewModel.effect,
                        onEventSent = viewModel::setEvent,
                        onNavigationRequested = { navigationEffect ->
                            when(navigationEffect) {
                                is NoteDescriptionContract.Effect.Navigation.PopBackStack -> navController.popBackStack()
                                is NoteDescriptionContract.Effect.Navigation.ToMap -> navController.navigate(Screen.Map.route)
                            }
                            /*navController.navigate(
                                when (navigationEffect) {
                                    is NoteDescriptionContract.Effect.Navigation.ToNotesList -> Screen.NotesList.route
                                }
                            )*/
                        }
                    )
                }
            }
        }
    )
}