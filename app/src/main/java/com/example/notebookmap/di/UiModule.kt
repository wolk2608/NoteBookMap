package com.example.notebookmap.di

import com.example.notebookmap.presentation.screens.map.MapViewModel
import com.example.notebookmap.presentation.screens.note_description.NoteDescriptionViewModel
import com.example.notebookmap.presentation.screens.notes_list.NotesListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { NotesListViewModel(repository = get()) }
    viewModel { MapViewModel(repository = get(), context = androidContext()) }
    viewModel { (noteId: Long) ->
        NoteDescriptionViewModel(
            repository = get(),
            noteId = noteId
        )
    }
}