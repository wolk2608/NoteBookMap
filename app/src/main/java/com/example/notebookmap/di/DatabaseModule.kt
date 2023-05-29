package com.example.notebookmap.di

import androidx.room.Room
import com.example.notebookmap.data.local.NoteBookDatabase
import com.example.notebookmap.utils.Constants.NOTE_BOOK_DATABASE
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            NoteBookDatabase::class.java,
            NOTE_BOOK_DATABASE
        //).build()
        ).createFromAsset("database/note_book_database.db").build()
        //).fallbackToDestructiveMigration().createFromAsset("database/note_book_database.db").build()
    }
}