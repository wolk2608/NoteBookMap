package com.example.notebookmap.di

import androidx.datastore.dataStore
import com.example.notebookmap.data.repository.DataSource
import com.example.notebookmap.data.repository.DataStore
import com.example.notebookmap.domain.repository.Repository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { DataStore(context = androidContext()) }
    single { DataSource(database = get()) }
    single { Repository(dataSource = get(), dataStore = get()) }
}