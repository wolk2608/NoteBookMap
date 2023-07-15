package com.example.notebookmap.navigation.paths

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.notebookmap.navigation.Screen
import com.example.notebookmap.navigation.defaultEnter
import com.example.notebookmap.navigation.defaultExit
import com.example.notebookmap.presentation.screens.note_description.NoteDescriptionContract
import com.example.notebookmap.presentation.screens.note_description.NoteDescriptionScreen
import com.example.notebookmap.presentation.screens.note_description.NoteDescriptionViewModel
import com.example.notebookmap.utils.Constants
import com.google.accompanist.navigation.animation.composable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.noteDescriptionNav(navController: NavController) {
    composable(
        route = Screen.NoteDescription.route,
        enterTransition = { defaultEnter },
        exitTransition = { defaultExit },
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
            }
        )
    }
}