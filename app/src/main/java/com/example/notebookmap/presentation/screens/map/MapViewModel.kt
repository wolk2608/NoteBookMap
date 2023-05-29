package com.example.notebookmap.presentation.screens.map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewModelScope
import com.example.notebookmap.R
import com.example.notebookmap.domain.models.Note
import com.example.notebookmap.domain.repository.Repository
import com.example.notebookmap.presentation.base.BaseViewModel
import com.example.notebookmap.utils.Constants
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MapViewModel(private val repository: Repository, private val context: Context) :
    BaseViewModel<
            MapContract.Event,
            MapContract.State,
            MapContract.Effect>() {
    private lateinit var mapView: MapView
    private lateinit var mapObjectCollection: MapObjectCollection
    private val myPlacemarks: MutableList<PlacemarkMapObject> = mutableListOf()
    val iconStyle = IconStyle().setScale(0.3f)

    init {
        setState { copy(isLoading = true) }
        MapKitFactory.initialize(context)
        viewModelScope.launch { getNotes() }.invokeOnCompletion {
            setState { copy(isLoading = false) }
        }
    }

    override fun setInitialState() = MapContract.State(isLoading = true)

    override fun handleEvents(event: MapContract.Event) {
        when (event) {
            is MapContract.Event.OnCreateMapScreen -> {
                Log.d("Map onCreate", "Map onCreate")
            }

            is MapContract.Event.OnStartMapScreen -> {
                MapKitFactory.getInstance().onStart()
                mapView.onStart()
                setupMap()
                setEffect {
                    MapContract.Effect.HideModalBottomSheet
                }
                /*val imageProvider = AnimatedImageProvider.fromResource(context, R.drawable.maxwell)
                val animatedPlacemark =
                    mapObjects.addPlacemark(Point(repository.latitude, repository.longitude), imageProvider, iconStyle)
                animatedPlacemark.useAnimation().play()*/
                Log.d("Map onStart", "Map onStart")
            }

            is MapContract.Event.OnResumeMapScreen -> {
                //temp solution
                viewModelScope.launch { getNotes() }
                mapObjectCollection.clear()
                myPlacemarks.clear()
                setupMap()
                /*val notes = viewState.value.notes
                val noteIdList: MutableList<Long> = mutableListOf()
                notes.forEach { noteIdList.add(it.id) }
                myPlacemarks.forEach {
                    val userData = it.userData as notePlacemarkMapObjectUserData
                    if (!noteIdList.contains(userData.id)) mapView.map.mapObjects.remove(it)
                }*/
                Log.d("Map onResume", "Map onResume")
            }

            is MapContract.Event.OnPauseMapScreen -> {
                // temp solution
                /*viewModelScope.launch { saveMapPosition() }.invokeOnCompletion {
                    MapKitFactory.getInstance().onStop()
                    mapView.onStop()
                    Log.d("Map onPause", "Map onPause")
                }*/
            }

            is MapContract.Event.OnStopMapScreen -> {
                viewModelScope.launch { saveMapPosition() }.invokeOnCompletion {
                    MapKitFactory.getInstance().onStop()
                    mapView.onStop()
                    Log.d("Map onStop", "Map onStop")
                }
            }

            is MapContract.Event.OnDestroyMapScreen -> {
                Log.d("Map onDestroy", "Map onDestroy")
            }

            is MapContract.Event.NotesSelection -> {
                setEffect {
                    MapContract.Effect.Navigation.ToNoteDescription(event.noteId)
                }
                setState {
                    copy(isLoading = !isLoading)
                }
            }

            is MapContract.Event.ToNoteDescription -> {
                setEffect {
                    MapContract.Effect.Navigation.ToNoteDescription(event.noteId)
                }
                Log.d("Map ToNoteDescription", "Map ToNoteDescription ${event.noteId}")
            }

            is MapContract.Event.SwitchAddingMode -> {
                setState { copy(inAdding = event.isAdding) }
            }

            is MapContract.Event.AddNote -> {
                viewModelScope.launch {
                    val id = repository.upsertNote(
                        Note(
                            noteLatitude = mapView.map.cameraPosition.target.latitude,
                            noteLongitude = mapView.map.cameraPosition.target.longitude,
                            noteTitle = "Новая заметка",
                            noteText = "",
                            noteDate = System.currentTimeMillis(),
                            noteTime = System.currentTimeMillis()
                        )
                    )
                    createNotePlacemark(
                        latitude = mapView.map.cameraPosition.target.latitude,
                        longitude = mapView.map.cameraPosition.target.longitude,
                        id = id,
                        title = "Новая заметка"
                    )
                }
                setState { copy(inAdding = event.isAdding) }
            }

            is MapContract.Event.SwitchEditingMode -> {
                setState { copy(inEditing = event.idEditing) }
                setEffect { MapContract.Effect.HideModalBottomSheet }
            }

            is MapContract.Event.SetNewNoteLocation -> {
                var isFind: Boolean = false
                myPlacemarks.forEach {
                    val userData = it.userData as notePlacemarkMapObjectUserData
                    if (userData.id == event.noteId) {
                        setState {
                            copy(
                                selectedNote = Note(
                                    id = selectedNote.id,
                                    noteTitle = selectedNote.noteTitle,
                                    noteText = selectedNote.noteText,
                                    noteLatitude = mapView.map.cameraPosition.target.latitude,
                                    noteLongitude = mapView.map.cameraPosition.target.longitude,
                                    noteDate = selectedNote.noteDate,
                                    noteTime = selectedNote.noteTime
                                )
                            )
                        }
                        viewModelScope.launch { upsertNote(viewState.value.selectedNote) }
                        val geometry = Point(
                            mapView.map.cameraPosition.target.latitude,
                            mapView.map.cameraPosition.target.longitude
                        )
                        it.geometry = geometry
                        setState { copy(inEditing = false) }
                        isFind = true
                    }
                }
                if (!isFind) {
                    Toast.makeText(
                        context,
                        "Ошибка! Такая метка не найдена.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            is MapContract.Event.SendMapView -> {
                mapView = event.mapView
            }

            is MapContract.Event.FindCurrentLocation -> {
                val locationManager = MapKitFactory.getInstance().createLocationManager()
                locationManager.requestSingleUpdate(object : LocationListener {
                    override fun onLocationStatusUpdated(p0: LocationStatus) {
                        Log.d("LocationStatus", "No status")
                    }

                    override fun onLocationUpdated(p0: Location) {
                        setState { copy(inAnimation = true) }
                        mapView.map.move(
                            CameraPosition(
                                Point(p0.position.latitude, p0.position.longitude),
                                15.0f,
                                0.0f,
                                0.0f
                            ),
                            Animation(Animation.Type.SMOOTH, 0.3f),
                            Map.CameraCallback {
                                setState { copy(inAnimation = false) }
                            }
                        )
                    }
                })
            }

            is MapContract.Event.Zoom -> {
                if (!viewState.value.inAnimation) {
                    setState { copy(inAnimation = true) }
                    iconStyle.scale = 1.0f
                    mapView.map.move(
                        CameraPosition(
                            Point(
                                mapView.map.cameraPosition.target.latitude,
                                mapView.map.cameraPosition.target.longitude
                            ),
                            mapView.map.cameraPosition.zoom.plus(event.zoomLevel),
                            mapView.map.cameraPosition.azimuth,
                            mapView.map.cameraPosition.tilt
                        ),
                        Animation(Animation.Type.SMOOTH, 0.3f),
                        Map.CameraCallback {
                            setState { copy(inAnimation = false) }
                        }
                    )
                }
            }

            is MapContract.Event.ChangeMapOrientation -> {
                if (!viewState.value.inAnimation) {
                    setState { copy(inAnimation = true) }
                    mapView.map.move(
                        CameraPosition(
                            Point(
                                mapView.map.cameraPosition.target.latitude,
                                mapView.map.cameraPosition.target.longitude
                            ),
                            mapView.map.cameraPosition.zoom,
                            event.azimuth,
                            mapView.map.cameraPosition.tilt
                        ),
                        Animation(Animation.Type.SMOOTH, 0.3f),
                        Map.CameraCallback {
                            setState { copy(inAnimation = false) }
                        }
                    )
                }
            }
        }
    }

    private suspend fun getNotes() {
        val notes = repository.notes.onEach { notes ->
            setState { copy(notes = notes) }
            notes.onEach { note ->
                createNotePlacemark(
                    latitude = note.noteLatitude!!,
                    longitude = note.noteLongitude!!,
                    id = note.id,
                    title = note.noteTitle
                )
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun getNoteById(id: Long) {
        val note = repository.getNoteById(id = id).first()
        setState { copy(selectedNote = note) }
        Log.d("NoteMap getNoteById", "$note")
    }

    private suspend fun upsertNote(note: Note) {
        repository.upsertNote(note)
    }

    private fun setupMap() {
        mapObjectCollection = mapView.map.mapObjects.addCollection()
        mapView.map.move(
            CameraPosition(
                Point(repository.latitude, repository.longitude),
                repository.zoom,
                repository.azimuth,
                repository.tilt
            )
        )
        Log.d("setupMap()", "${viewState.value.notes}")
        viewState.value.notes.onEach {
            createNotePlacemark(
                latitude = it.noteLatitude!!,
                longitude = it.noteLongitude!!,
                id = it.id,
                title = it.noteTitle
            )
        }
    }

    private suspend fun saveMapPosition() {
        repository.saveMapPosition(
            mapView.map.cameraPosition.target.latitude,
            mapView.map.cameraPosition.target.longitude,
            mapView.map.cameraPosition.zoom,
            mapView.map.cameraPosition.azimuth,
            mapView.map.cameraPosition.tilt
        )
    }

    private class notePlacemarkMapObjectUserData(val id: Long, val title: String)

    private fun createNotePlacemark(latitude: Double, longitude: Double, id: Long, title: String) {
        val notePlacemark: PlacemarkMapObject = mapObjectCollection.addPlacemark(
            Point(latitude, longitude),
            ImageProvider.fromBitmap(
                (ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_location,
                    null
                ) as VectorDrawable).toBitmap()
            )
        )
        notePlacemark.zIndex = 100.0f
        notePlacemark.setText(
            title,
            TextStyle()
                .setPlacement(TextStyle.Placement.BOTTOM_RIGHT)
                .setSize(10.0f)
                .setOutlineColor(Color.WHITE)
        )
        notePlacemark.userData = notePlacemarkMapObjectUserData(id = id, title = title)
        notePlacemark.addTapListener(notePlacemarkMapObjectTapListener)
        myPlacemarks.add(notePlacemark)
    }

    private val notePlacemarkMapObjectTapListener =
        MapObjectTapListener { mapObject, point ->
            if (mapObject is PlacemarkMapObject) {
                val notePlacemark = mapObject
                val userData = notePlacemark.userData
                if (userData is notePlacemarkMapObjectUserData) {
                    val notePlacemarkUserData = userData
                    /*Toast.makeText(
                        context,
                        "NotePlacemark with id " + notePlacemarkUserData.id + " and title '" + notePlacemarkUserData.title + "' tapped",
                        Toast.LENGTH_SHORT
                    ).show()*/
                    viewModelScope.launch {
                        getNoteById(notePlacemarkUserData.id)
                        Log.d("NoteMap Event NotesSelection", "${viewState.value.selectedNote}")
                    }
                    setEffect { MapContract.Effect.ShowModalBottomSheet }
                    setState { copy(inEditing = false) }
                }
            }
            true
        }
}