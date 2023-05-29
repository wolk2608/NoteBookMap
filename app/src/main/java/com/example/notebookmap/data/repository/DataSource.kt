package com.example.notebookmap.data.repository

import com.example.notebookmap.data.local.NoteBookDatabase
import com.example.notebookmap.domain.models.Note
import com.example.notebookmap.domain.models.NoteMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSource (database : NoteBookDatabase) {
    private val dao = database.dao()
    private val coroutineContext = Dispatchers.IO

    val notes get() = dao.searchNotes("")
    fun searchNotes(text: String) = dao.searchNotes(text = text)

    suspend fun upsertNote(note : Note) = withContext(context = coroutineContext) {
        dao.upsertNote(note = note)
    }

    suspend fun upsertNoteMedia(noteMedia : NoteMedia) = withContext(context = coroutineContext) {
        dao.upsertNoteMedia(noteMedia = noteMedia)
    }

    suspend fun deleteNoteById(id: Long) = withContext(context = coroutineContext) {
        dao.deleteNoteById(id = id)
    }

    suspend fun deleteNoteMedia(noteMedia: NoteMedia) = withContext(context = coroutineContext) {
        dao.deleteNoteMedia(noteMedia = noteMedia)
    }

    fun getNoteById(id: Long) = dao.getNoteById(id = id)
    fun getPhotosByNoteId(id: Long) = dao.getPhotosByNoteId(id = id)
    fun getVideosByNoteId(id: Long) = dao.getVideosByNoteId(id = id)
    fun getAudiosByNoteId(id: Long) = dao.getAudiosByNoteId(id = id)
}