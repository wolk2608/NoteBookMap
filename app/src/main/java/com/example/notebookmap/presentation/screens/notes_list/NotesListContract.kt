package com.example.notebookmap.presentation.screens.notes_list

import com.example.notebookmap.domain.models.Note
import com.example.notebookmap.presentation.base.ViewEvent
import com.example.notebookmap.presentation.base.ViewSideEffect
import com.example.notebookmap.presentation.base.ViewState

class NotesListContract {
    sealed class Event : ViewEvent {
        data class NotesSelection(val noteId: Long) : Event()
    }

    data class State(val notes: List<Note> = emptyList(), val isLoading: Boolean = false) : ViewState

    sealed class Effect : ViewSideEffect {
        object ToastDataWasLoaded : Effect()

        sealed class Navigation : Effect() {
            data class ToNoteDescription(val noteId: Long) : Navigation()
        }
    }
}