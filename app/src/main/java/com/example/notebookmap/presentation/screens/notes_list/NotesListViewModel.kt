package com.example.notebookmap.presentation.screens.notes_list

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.notebookmap.domain.repository.Repository
import com.example.notebookmap.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NotesListViewModel (private val repository: Repository) :
    BaseViewModel<
            NotesListContract.Event,
            NotesListContract.State,
            NotesListContract.Effect>() {

    init {
        setState { copy(isLoading = true) }
        viewModelScope.launch { getNotes() }.invokeOnCompletion {
            setState { copy(isLoading = false) }
            Log.d("NotesListInit", "${viewState.value.notes}")
        }

    }

    override fun setInitialState() =
        NotesListContract.State(isLoading = true)

    override fun handleEvents(event: NotesListContract.Event) {
        when (event) {
            is NotesListContract.Event.NotesSelection -> {
                setEffect {
                    NotesListContract.Effect.Navigation.ToNoteDescription(event.noteId)
                }
                setState {
                    copy(isLoading = !isLoading)
                }
                Log.d("NotesSelectionEvent", "${viewState.value.notes}")
            }
        }
    }

    private suspend fun getNotes() {
        val notes = repository.notes.onEach {
            setState {
                copy(notes = it)
            }
        }.launchIn(viewModelScope)
    }
}