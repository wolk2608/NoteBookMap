package com.example.notebookmap.navigation.paths

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.notebookmap.navigation.Screen
import com.example.notebookmap.navigation.defaultEnter
import com.example.notebookmap.navigation.defaultExit
import com.example.notebookmap.presentation.screens.notes_list.NotesListContract
import com.example.notebookmap.presentation.screens.notes_list.NotesListScreen
import com.example.notebookmap.presentation.screens.notes_list.NotesListViewModel
import com.google.accompanist.navigation.animation.composable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.noteListNav(navController: NavController) {
    composable(
        route = Screen.NotesList.route,
        enterTransition = { defaultEnter },
        exitTransition = { defaultExit }
    ) {
        val viewModel = koinViewModel<NotesListViewModel>()
        NotesListScreen(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = viewModel::setEvent,
            onNavigationRequested = { navigationEffect ->
                navController.navigate(
                    when (navigationEffect) {
                        is NotesListContract.Effect.Navigation.ToNoteDescription -> Screen.NoteDescription.passParam(navigationEffect.noteId)
                    }
                )
            }
        )
    }
}