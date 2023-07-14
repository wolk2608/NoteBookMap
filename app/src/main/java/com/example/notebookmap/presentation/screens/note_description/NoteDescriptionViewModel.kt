package com.example.notebookmap.presentation.screens.note_description

import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.example.notebookmap.domain.models.Note
import com.example.notebookmap.domain.models.NoteMedia
import com.example.notebookmap.domain.repository.Repository
import com.example.notebookmap.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NoteDescriptionViewModel(private val repository: Repository, noteId: Long) :
    BaseViewModel<
            NoteDescriptionContract.Event,
            NoteDescriptionContract.State,
            NoteDescriptionContract.Effect>() {

    private var noteLoaded: Boolean = false
    private var photoLoaded: Boolean = false
    private var videoLoaded: Boolean = false
    private var audioLoaded: Boolean = false

    init {
        setState { copy(isLoading = true) }
        /*viewModelScope.launch { if (!noteLoaded) { getNoteById(noteId); noteLoaded = true } }
        viewModelScope.launch { if (!photoLoaded) { getPhotosByNoteId(noteId); photoLoaded = true } }
        viewModelScope.launch { if (!videoLoaded) { getVideosByNoteId(noteId); videoLoaded = true } }
        viewModelScope.launch { if (!audioLoaded) { getAudiosByNoteId(noteId); audioLoaded = true } }
        setState { copy(isLoading = false) }
        Log.d("NoteDescriptionInit", "${viewState.value.note}")*/
        viewModelScope.launch {
            if (!noteLoaded) {
                getNoteById(noteId); noteLoaded = true
            }
            if (!photoLoaded) {
                getPhotosByNoteId(noteId); photoLoaded = true
            }
            if (!videoLoaded) {
                getVideosByNoteId(noteId); videoLoaded = true
            }
            if (!audioLoaded) {
                getAudiosByNoteId(noteId); audioLoaded = true
            }
            setState { copy(isLoading = false) }
            Log.d("NoteDescriptionInit", "${viewState.value.note}")
        }
    }
    override fun setInitialState() = NoteDescriptionContract.State()

    override fun handleEvents(event: NoteDescriptionContract.Event) { // вынести в функции
        when (event) {
            is NoteDescriptionContract.Event.AttemptToClose -> { // UI не должен контролировать состояние загрузки
                setState { copy(isLoading = !isLoading) }
                Log.d("NotesSelectionEvent", "${viewState.value.note}")
            }

            is NoteDescriptionContract.Event.PopBackStack -> {
                // TODO Спросить о сохранении перед выходом
                setEffect {
                    NoteDescriptionContract.Effect.Navigation.PopBackStack
                }
            }

            is NoteDescriptionContract.Event.SaveNote -> {
                viewModelScope.launch { upsertNote() }
                viewModelScope.launch { upsertNoteMedia() }
                viewModelScope.launch { deleteUnusedNoteMedia() }
                setEffect {
                    NoteDescriptionContract.Effect.Navigation.PopBackStack
                }
            }

            is NoteDescriptionContract.Event.DeleteNote -> {
                viewModelScope.launch {
                    repository.deleteNoteById(viewState.value.note.id)
                    setEffect {
                        NoteDescriptionContract.Effect.Navigation.PopBackStack
                    }
                }
            }

            is NoteDescriptionContract.Event.OpenNoteInMap -> {
                // TODO Спросить о сохранении перед выходом
                viewModelScope.launch {
                    updateMapPosition()
                }.invokeOnCompletion {
                    setEffect {
                        NoteDescriptionContract.Effect.Navigation.ToMap
                    }
                }
            }

            is NoteDescriptionContract.Event.ChangeNoteTitle -> {
                setState { copy(newNoteTitle = event.newNoteTitle) }
            }

            is NoteDescriptionContract.Event.ChangeNoteText -> {
                setState { copy(newNoteText = event.newNoteText) }
            }

            is NoteDescriptionContract.Event.AddPhotos -> {
                val containsPhotos = viewState.value.newPhotosUri
                val addedPhotos = event.newPhotos.forEach {
                    if (!containsPhotos.contains(it.toString())) {
                        containsPhotos.add(it.toString())
                    }
                }
                setState {
                    copy(newPhotosUri = containsPhotos)
                }
                Log.d("NoteDescription AddPhotos", "${event.newPhotos}")
            }

            is NoteDescriptionContract.Event.AddVideos -> {
                val containsVideos = viewState.value.newVideosUri
                val addedVideos = event.newVideos.forEach {
                    if (!containsVideos.contains(it.toString())) {
                        containsVideos.add(it.toString())
                    }
                }
                setState {
                    copy(newVideosUri = containsVideos)
                }
                Log.d("NoteDescription AddVideos", "${event.newVideos}")
            }

            is NoteDescriptionContract.Event.AddAudios -> {
                val containsAudios = viewState.value.newAudiosUri
                val addedAudios = event.newAudios.forEach {
                    if (!containsAudios.contains(it.toString())) {
                        containsAudios.add(it.toString())
                    }
                }
                setState {
                    copy(newAudiosUri = containsAudios)
                }
                Log.d("NoteDescription AddAudios", "${event.newAudios}")
            }

            is NoteDescriptionContract.Event.DeletePhoto -> {
                val newPhotosUri = viewState.value.newPhotosUri
                newPhotosUri.remove(event.photoUri)
                setState {
                    copy(newPhotosUri = newPhotosUri)
                }
            }

            is NoteDescriptionContract.Event.DeleteVideo -> {
                val newVideosUri = viewState.value.newVideosUri
                newVideosUri.remove(event.videoUri)
                setState {
                    copy(newVideosUri = newVideosUri)
                }
            }

            is NoteDescriptionContract.Event.DeleteAudio -> {
                val newAudiosUri = viewState.value.newAudiosUri
                newAudiosUri.remove(event.audioUri)
                setState {
                    copy(newAudiosUri = newAudiosUri)
                }
            }
        }
    }

    private suspend fun getNoteById(id: Long) {
        /*if (!noteLoaded) {
            val note = repository.getNoteById(id = id).onEach {
                setState { copy(note = it) }
                setState { copy(newNoteTitle = it.noteTitle) }
                setState { copy(newNoteText = it.noteText!!) }
                Log.d("NoteDescription getNote", "$it")
            }.launchIn(viewModelScope)
        }*/
        if (!noteLoaded) {
            val note = repository.getNoteById(id = id).first()
            setState { copy(note = note) } // ??
            setState { copy(newNoteTitle = note.noteTitle) } // ??
            setState { copy(newNoteText = note.noteText!!) } // ??
            Log.d("NoteDescription getNote", "$note")
        }
    }

    private suspend fun getPhotosByNoteId(id: Long) {
        /*if (!photoLoaded) {
            val photos = repository.getPhotosByNoteId(id = id).onEach {
                setState { copy(sourcePhotosUri = it) }
                setState { copy(newPhotosUri = it.toMutableStateList()) }
                Log.d("NoteDescription getPhotos", "$it")
            }.launchIn(viewModelScope)
        }*/
        if (!photoLoaded) {
            val photos = repository.getPhotosByNoteId(id = id).first()
            Log.d("NoteDescription getPhotos", "$photos")
            setState {
                copy(
                    sourcePhotosUri = photos,
                    newPhotosUri = photos.toMutableStateList()
                )
            }
        }
    }

    private suspend fun getVideosByNoteId(id: Long) {
        /*if (!videoLoaded) {
            val videos = repository.getVideosByNoteId(id = id).first()
                setState { copy(sourceVideosUri = videos) }
                setState { copy(newVideosUri = videos.toMutableStateList()) }
                Log.d("NoteDescription getVideos", "$videos")
        }*/
        if (!videoLoaded) {
            val videos = repository.getVideosByNoteId(id = id).first()
                setState { copy(sourceVideosUri = videos) }
                setState { copy(newVideosUri = videos.toMutableStateList()) }
                Log.d("NoteDescription getVideos", "$videos")
        }
    }

    private suspend fun getAudiosByNoteId(id: Long) {
        /*if (!audioLoaded) {
            val audios = repository.getAudiosByNoteId(id = id).onEach {
                setState { copy(sourceAudiosUri = it) }
                setState { copy(newAudiosUri = it.toMutableStateList()) }
                Log.d("NoteDescription getAudios", "$it")
            }.launchIn(viewModelScope)
        }*/
        if (!audioLoaded) {
            val audios = repository.getAudiosByNoteId(id = id).first()
                setState { copy(sourceAudiosUri = audios) } // ??
                setState { copy(newAudiosUri = audios.toMutableStateList()) } // ??
                Log.d("NoteDescription getAudios", "$audios")
        }
    }

    private suspend fun upsertNoteMedia() {
        val id = viewState.value.note.id
        val tempPhotos = viewState.value.newPhotosUri.toList()
        val tempVideos = viewState.value.newVideosUri.toList()
        val tempAudios = viewState.value.newAudiosUri.toList()

        tempPhotos.forEach {
            val tempPhoto = NoteMedia(noteId = id, mediaUri = it, typeId = 1)
            repository.upsertNoteMedia(tempPhoto)
            Log.d("NoteDescription save photo", "${NoteMedia(noteId = id, mediaUri = it, typeId = 1)}")
        }
        Log.d("NoteDescription save video", "$tempVideos")
        tempVideos.forEach{
            val tempVideo = NoteMedia(noteId = id, mediaUri = it, typeId = 2)
            repository.upsertNoteMedia(tempVideo)
            Log.d("NoteDescription save video", "${NoteMedia(noteId = id, mediaUri = it, typeId = 2)}")
        }
        tempAudios.forEach {
            val tempAudio = NoteMedia(noteId = id, mediaUri = it, typeId = 3)
            repository.upsertNoteMedia(tempAudio)
            Log.d("NoteDescription save audio", "${NoteMedia(noteId = id, mediaUri = it, typeId = 3)}")
        }


        /*viewState.value.newPhotosUri.forEach {
            repository.upsertNoteMedia(NoteMedia(noteId = viewState.value.note.id, mediaUri = it, 1))
            Log.d("NoteDescription save photo", "${NoteMedia(noteId = viewState.value.note.id, mediaUri = it, 1)}")
        }
        Log.d("NoteDescription save video", "${viewState.value.newVideosUri.toList()}")
        viewState.value.newVideosUri.forEach{
            repository.upsertNoteMedia(NoteMedia(noteId = viewState.value.note.id, mediaUri = it, 2))
            Log.d("NoteDescription save video", "${NoteMedia(noteId = viewState.value.note.id, mediaUri = it, 2)}")
        }
        viewState.value.newAudiosUri.forEach {
            repository.upsertNoteMedia(NoteMedia(noteId = viewState.value.note.id, mediaUri = it, 3))
            Log.d("NoteDescription save audio", "${NoteMedia(noteId = viewState.value.note.id, mediaUri = it, 3)}")
        }*/
    }

    private suspend fun deleteUnusedNoteMedia() {
        val unusedMedia: MutableList<NoteMedia> = mutableListOf()
        viewState.value.sourcePhotosUri.onEach {
            if (!viewState.value.newPhotosUri.contains(it)) {
                unusedMedia.add(NoteMedia(noteId = viewState.value.note.id, mediaUri = it, typeId = 1))
            }
        }
        viewState.value.sourceVideosUri.onEach {
            if (!viewState.value.newVideosUri.contains(it)) {
                unusedMedia.add(NoteMedia(noteId = viewState.value.note.id, mediaUri = it, typeId = 2))
            }
        }
        viewState.value.sourceAudiosUri.onEach {
            if (!viewState.value.newAudiosUri.contains(it)) {
                unusedMedia.add(NoteMedia(noteId = viewState.value.note.id, mediaUri = it, typeId = 3))
            }
        }
        unusedMedia.onEach {
            repository.deleteNoteMedia(it)
        }
    }

    private suspend fun upsertNote() {
        val newNote: Note = Note(
            id = viewState.value.note.id,
            noteTitle = viewState.value.newNoteTitle,
            noteText = viewState.value.newNoteText,
            noteLatitude = viewState.value.note.noteLatitude,
            noteLongitude = viewState.value.note.noteLongitude,
            noteDate = viewState.value.note.noteDate,
            noteTime = viewState.value.note.noteTime
        )
        repository.upsertNote(newNote)
        Log.d("upsertNote", "$newNote")
    }

    private suspend fun updateMapPosition() {
        repository.saveMapPosition(
            latitude = viewState.value.note.noteLatitude!!,
            longitude = viewState.value.note.noteLongitude!!,
            15.0f,
            0.0f,
            0.0f
        )
    }
}