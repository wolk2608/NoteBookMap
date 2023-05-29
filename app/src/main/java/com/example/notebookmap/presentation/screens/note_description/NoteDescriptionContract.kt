package com.example.notebookmap.presentation.screens.note_description

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.notebookmap.domain.models.Note
import com.example.notebookmap.presentation.base.ViewEvent
import com.example.notebookmap.presentation.base.ViewSideEffect
import com.example.notebookmap.presentation.base.ViewState

class NoteDescriptionContract {
    sealed class Event : ViewEvent {
        data class AttemptToClose(val noteId: Long) : Event()
        data class ChangeNoteTitle(val newNoteTitle: String) : Event()
        data class ChangeNoteText(val newNoteText: String) : Event()
        object PopBackStack : Event()
        object SaveNote : Event()
        object DeleteNote : Event()
        object OpenNoteInMap : Event()
        data class AddPhotos(val newPhotos: List<Uri>) : Event()
        data class AddVideos(val newVideos: List<Uri>) : Event()
        data class AddAudios(val newAudios: List<Uri>) : Event()
        data class DeletePhoto(val photoUri: String) : Event()
        data class DeleteVideo(val videoUri: String) : Event()
        data class DeleteAudio(val audioUri: String) : Event()
    }

    data class State(
        val note: Note =
            Note(
                noteLatitude = 1.0,
                noteLongitude = 1.0,
                noteTitle = "Новая заметка",
                noteText = "",
                noteDate = System.currentTimeMillis(),
                noteTime = System.currentTimeMillis()
            ),
        val sourcePhotosUri: List<String> = emptyList(),
        val sourceVideosUri: List<String> = emptyList(),
        val sourceAudiosUri: List<String> = emptyList(),
        val newNoteTitle: String = "Новая заметка",
        val newNoteText: String = "",
        val newPhotosUri: SnapshotStateList<String> = mutableStateListOf(),
        val newVideosUri: SnapshotStateList<String> = mutableStateListOf(),
        val newAudiosUri: SnapshotStateList<String> = mutableStateListOf(),
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        object ToastDataWasLoaded : Effect()

        sealed class Navigation : Effect() {
            object PopBackStack : Navigation()
            object ToMap : Navigation()
        }
    }
}