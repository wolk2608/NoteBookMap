package com.example.notebookmap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notebookmap.domain.models.Note
import com.example.notebookmap.domain.models.NoteMedia
import com.example.notebookmap.domain.models.Type

@Database(
    entities = [
        Note::class,
        Type::class,
        NoteMedia::class
    ],
    version = 2
)
abstract class NoteBookDatabase: RoomDatabase() {
    abstract fun dao(): NoteBookDao
}