package com.example.notebookmap.presentation.screens.map

import com.example.notebookmap.domain.models.Note
import com.example.notebookmap.presentation.base.ViewEvent
import com.example.notebookmap.presentation.base.ViewSideEffect
import com.example.notebookmap.presentation.base.ViewState
import com.example.notebookmap.utils.Constants.DEFAULT_LATITUDE
import com.example.notebookmap.utils.Constants.DEFAULT_LONGITUDE
import com.yandex.mapkit.mapview.MapView

class MapContract {
    sealed class Event : ViewEvent {
        data class NotesSelection(val noteId: Long) : Event()
        data class SwitchAddingMode(val isAdding: Boolean) : Event()
        data class AddNote(val isAdding: Boolean) : Event()
        data class SwitchEditingMode(val idEditing: Boolean) : Event()
        data class SetNewNoteLocation(val noteId: Long) : Event()
        data class ToNoteDescription(val noteId: Long) : Event()

        data class SendMapView(val mapView: MapView) : Event()
        object FindCurrentLocation : Event()
        data class Zoom(val zoomLevel: Float) : Event()
        data class ChangeMapOrientation(val azimuth: Float) : Event()

        object OnCreateMapScreen : Event()
        object OnStartMapScreen : Event()
        object OnResumeMapScreen : Event()
        object OnPauseMapScreen : Event()
        object OnStopMapScreen : Event()
        object OnDestroyMapScreen : Event()
    }

    data class State(
        val notes: List<Note> = emptyList(),
        val isLoading: Boolean = true,
        val inAnimation: Boolean = false,
        val inAdding: Boolean = false,
        val inEditing: Boolean = false,
        val selectedNote: Note =
            Note(
                noteLatitude = DEFAULT_LATITUDE,
                noteLongitude = DEFAULT_LONGITUDE,
                noteTitle = "Новая заметка",
                noteText = "",
                noteDate = System.currentTimeMillis(),
                noteTime = System.currentTimeMillis()
            )
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        sealed class Navigation : Effect() {
            data class ToNoteDescription(val noteId: Long) : Navigation()
        }
        object ShowModalBottomSheet: Effect()
        object HideModalBottomSheet: Effect()
    }
}