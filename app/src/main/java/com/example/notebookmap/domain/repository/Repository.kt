package com.example.notebookmap.domain.repository

import com.example.notebookmap.data.repository.DataSource
import com.example.notebookmap.data.repository.DataStore
import com.example.notebookmap.domain.models.Note
import com.example.notebookmap.domain.models.NoteMedia

class Repository (private val dataSource : DataSource, private val dataStore : DataStore) {
    //dataStore
    val latitude get() = dataStore.syncMapLatitude
    val longitude get() = dataStore.syncMapLongitude
    val zoom get() = dataStore.syncMapZoom
    val azimuth get() = dataStore.syncMapAzimuth
    val tilt get() = dataStore.syncMapTilt

    suspend fun saveMapPosition(latitude : Double, longitude : Double, zoom : Float, azimuth : Float, tilt : Float) {
        dataStore.saveMapLatitude(latitude)
        dataStore.saveMapLongitude(longitude)
        dataStore.saveMapZoom(zoom)
        dataStore.saveMapAzimuth(azimuth)
        dataStore.saveMapTilt(tilt)
    }

    suspend fun updateMapPosition(latitude : Double, longitude : Double) {
        dataStore.saveMapLatitude(latitude)
        dataStore.saveMapLongitude(longitude)
    }

    //dataSource
    val notes get() = dataSource.notes

    fun searchNotes(text: String) = dataSource.searchNotes(text = text)

    suspend fun upsertNote(note : Note) = dataSource.upsertNote(note = note)

    suspend fun upsertNoteMedia(noteMedia : NoteMedia) = dataSource.upsertNoteMedia(noteMedia = noteMedia)

    suspend fun deleteNoteById(id: Long) = dataSource.deleteNoteById(id = id)

    suspend fun deleteNoteMedia(noteMedia: NoteMedia) = dataSource.deleteNoteMedia(noteMedia = noteMedia)

    fun getNoteById(id: Long) = dataSource.getNoteById(id = id)
    fun getPhotosByNoteId(id: Long) = dataSource.getPhotosByNoteId(id = id)
    fun getVideosByNoteId(id: Long) = dataSource.getVideosByNoteId(id = id)
    fun getAudiosByNoteId(id: Long) = dataSource.getAudiosByNoteId(id = id)
}