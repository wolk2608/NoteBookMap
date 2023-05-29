package com.example.notebookmap

import android.app.Application
import com.example.notebookmap.di.dataModule
import com.example.notebookmap.di.databaseModule
import com.example.notebookmap.di.uiModule
import com.example.notebookmap.utils.Constants.MAPKIT_API_KEY
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            MapKitFactory.setApiKey(MAPKIT_API_KEY)
            modules(
                databaseModule,
                dataModule,
                uiModule
            )
        }
    }
}