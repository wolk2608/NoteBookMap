package com.example.notebookmap.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.notebookmap.utils.Constants.DEFAULT_LATITUDE
import com.example.notebookmap.utils.Constants.DEFAULT_LONGITUDE
import com.example.notebookmap.utils.Constants.PREFERENCES_NAME
import com.yandex.mapkit.map.Map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class DataStore(context: Context) {

    companion object {
        private const val SHIFT_KEY = "is_shift_open"
        private const val MAP_LATITUDE = "map_latitude"
        private const val MAP_LONGITUDE = "map_longitude"
        private const val MAP_ZOOM = "map_zoom"
        private const val MAP_AZIMUTH = "map_azimuth"
        private const val MAP_TILT = "map_tilt"
    }

    private object PreferencesKey {
        val isShiftOpenKey = booleanPreferencesKey(name = SHIFT_KEY)
        val mapLatitudeKey = doublePreferencesKey(name = MAP_LATITUDE)
        val mapLongitudeKey = doublePreferencesKey(name = MAP_LONGITUDE)
        val mapZoomKey = floatPreferencesKey(name = MAP_ZOOM)
        val mapAzimuthKey = floatPreferencesKey(name = MAP_AZIMUTH)
        val mapTiltKey = floatPreferencesKey(name = MAP_TILT)
    }

    private val dataStore = context.dataStore
    private val coroutineContext = Dispatchers.IO

    suspend fun saveShift(isOpen: Boolean) = withContext(coroutineContext) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.isShiftOpenKey] = isOpen
        }
    }

    val shift get() = dataStore.data
        .catch { emptyPreferences() }
        .map { it[PreferencesKey.isShiftOpenKey] ?: false }

    val syncShift get() = runBlocking(coroutineContext) { dataStore.data.first()[PreferencesKey.isShiftOpenKey] ?: false }

    suspend fun saveMapLatitude(latitude: Double) = withContext(coroutineContext) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.mapLatitudeKey] = latitude
        }
    }

    val mapLatitude get() = dataStore.data
        .catch { emptyPreferences() }
        .map {
            it[PreferencesKey.mapLatitudeKey] ?: false
        }

    val syncMapLatitude get() = runBlocking(coroutineContext) { dataStore.data.first()[PreferencesKey.mapLatitudeKey] ?: DEFAULT_LATITUDE }

    suspend fun saveMapLongitude(longitude: Double) = withContext(coroutineContext) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.mapLongitudeKey] = longitude
        }
    }

    val mapLongitude get() = dataStore.data
        .catch { emptyPreferences() }
        .map {
            it[PreferencesKey.mapLongitudeKey] ?: false
        }

    val syncMapLongitude get() = runBlocking(coroutineContext) { dataStore.data.first()[PreferencesKey.mapLongitudeKey] ?: DEFAULT_LONGITUDE }

    suspend fun saveMapZoom(zoom: Float) = withContext(coroutineContext) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.mapZoomKey] = zoom
        }
    }

    val mapZoom get() = dataStore.data
        .catch { emptyPreferences() }
        .map {
            it[PreferencesKey.mapZoomKey] ?: false
        }

    val syncMapZoom get() = runBlocking(coroutineContext) { dataStore.data.first()[PreferencesKey.mapZoomKey] ?: 15.0f }

    suspend fun saveMapAzimuth(azimuth: Float) = withContext(coroutineContext) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.mapAzimuthKey] = azimuth
        }
    }

    val mapAzimuth get() = dataStore.data
        .catch { emptyPreferences() }
        .map {
            it[PreferencesKey.mapAzimuthKey] ?: false
        }

    val syncMapAzimuth get() = runBlocking(coroutineContext) { dataStore.data.first()[PreferencesKey.mapAzimuthKey] ?: 0.0f }

    suspend fun saveMapTilt(tilt: Float) = withContext(coroutineContext) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.mapTiltKey] = tilt
        }
    }

    val mapTilt get() = dataStore.data
        .catch { emptyPreferences() }
        .map {
            it[PreferencesKey.mapTiltKey] ?: false
        }

    val syncMapTilt get() = runBlocking(coroutineContext) { dataStore.data.first()[PreferencesKey.mapTiltKey] ?: 0.0f }
}

