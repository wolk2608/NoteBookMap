package com.example.notebookmap.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.notebookmap.domain.models.Note
import com.example.notebookmap.domain.models.NoteMedia
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteBookDao {
    @Query("SELECT * FROM note_table")
    fun getNotes() : Flow<List<Note>>

    @Query("SELECT * FROM note_table WHERE (note_title LIKE '%' || :text || '%' OR note_text LIKE '%' || :text || '%') ")
    fun searchNotes(text : String) : Flow<List<Note>>

    @Query("SELECT * FROM note_table WHERE (id = :id)")
    fun getNoteById(id : Long) : Flow<Note>

    @Query("SELECT media_uri FROM note_media_table WHERE (note_media_table.note_id = :id AND note_media_table.type_id = 1)")
    fun getPhotosByNoteId(id : Long) : Flow<List<String>>

    @Query("SELECT media_uri FROM note_media_table WHERE (note_media_table.note_id = :id AND note_media_table.type_id = 2)")
    fun getVideosByNoteId(id : Long) : Flow<List<String>>

    //@Query("SELECT * FROM note_media_table INNER JOIN note_media_table ON media_table.id = note_media_table.media_id WHERE (note_media_table.note_id = :id AND media_table.type_id = 3)")
    @Query("SELECT media_uri FROM note_media_table WHERE (note_media_table.note_id = :id AND note_media_table.type_id = 3)")
    fun getAudiosByNoteId(id : Long) : Flow<List<String>>

    @Upsert
    suspend fun upsertNote(note : Note) : Long

    @Upsert
    suspend fun upsertNoteMedia(noteMedia : NoteMedia)

    //@Query("INSERT INTO note_media_table VALUES (note_id = :id)")

    @Query("DELETE FROM note_table WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Delete
    suspend fun deleteNoteMedia(noteMedia : NoteMedia)
}