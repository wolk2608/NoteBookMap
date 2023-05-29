package com.example.notebookmap.presentation.screens.map

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.rememberImagePainter
import com.example.notebookmap.R
import com.example.notebookmap.presentation.components.CircleButton
import com.example.notebookmap.presentation.components.DescriptionButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    state: MapContract.State,
    effectFlow: Flow<MapContract.Effect>?,
    onEventSent: (event: MapContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: MapContract.Effect.Navigation) -> Unit
) {
    // Camera permission state
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (state.isLoading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(durationMillis = 500)
    )

    val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    // Listen for side effects from the VM
    LaunchedEffect(Unit) {
        Log.d("LaunchedEffect", "${state}")
        effectFlow?.onEach { effect ->
            when (effect) {
                is MapContract.Effect.Navigation.ToNoteDescription -> {
                    onNavigationRequested(effect)
                }

                is MapContract.Effect.ShowModalBottomSheet -> {
                    scope.launch {
                        modalBottomSheetState.show()
                    }
                }
                is MapContract.Effect.HideModalBottomSheet -> {
                    scope.launch {
                        modalBottomSheetState.hide()
                    }
                }
            }
        }?.collect()
    }

    OnLifecycleEvent { owner, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                onEventSent(MapContract.Event.OnCreateMapScreen)
            }
            Lifecycle.Event.ON_START -> {
                onEventSent(MapContract.Event.OnStartMapScreen)
            }
            Lifecycle.Event.ON_RESUME -> {
                onEventSent(MapContract.Event.OnResumeMapScreen)
            }
            Lifecycle.Event.ON_PAUSE -> {
                onEventSent(MapContract.Event.OnPauseMapScreen)
            }
            Lifecycle.Event.ON_STOP -> {
                onEventSent(MapContract.Event.OnStopMapScreen)
            }
            Lifecycle.Event.ON_DESTROY -> {
                onEventSent(MapContract.Event.OnDestroyMapScreen)
            }
            else -> { /* other stuff */
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                onEventSent(MapContract.Event.SendMapView(this))
            }
        }
    )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.TopEnd,
        ) {
            Column(
                modifier = Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                CircleButton(
                    onClicked = {
                        when (locationPermissionState.status) {
                            is PermissionStatus.Granted -> {
                                onEventSent(MapContract.Event.FindCurrentLocation)
                            }
                            else -> {
                                locationPermissionState.launchPermissionRequest()
                            }
                        }
                    },
                    textColor = Color.White,
                    image = ImageVector.vectorResource(R.drawable.ic_gps)
                )
                CircleButton(
                    onClicked = { onEventSent(MapContract.Event.Zoom(1.0f)) },
                    textColor = Color.White,
                    image = Icons.Filled.Add
                )
                CircleButton(
                    onClicked = { onEventSent(MapContract.Event.Zoom(-1.0f)) },
                    textColor = Color.White,
                    image = ImageVector.vectorResource(R.drawable.ic_remove)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, bottom = 25.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
                CircleButton(
                    onClicked = {
                        onEventSent(MapContract.Event.ChangeMapOrientation(0.0f))
                    },
                    textColor = Color.White,
                    image = ImageVector.vectorResource(R.drawable.ic_compass)
                )
        }

        if (!(state.inAdding || state.inEditing)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 10.dp, bottom = 25.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                CircleButton(
                    onClicked = { onEventSent(MapContract.Event.SwitchAddingMode(true)) },
                    textColor = Color.White,
                    image = Icons.Filled.AddLocation
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(MaterialTheme.typography.headlineLarge.fontSize.value.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_crosshair),
                    contentDescription = "crosshair",
                    tint = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 75.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 100.dp, minHeight = 50.dp)
                                .border(
                                    border = BorderStroke(2.dp, Color.Black),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            ),
                            onClick = {
                                if (state.inAdding) onEventSent(MapContract.Event.SwitchAddingMode(false))
                                if (state.inEditing) onEventSent(MapContract.Event.SwitchAddingMode(false))
                            }
                        ) {
                            Text(text = "Отмена")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 100.dp, minHeight = 50.dp)
                                .border(
                                    border = BorderStroke(2.dp, Color.Black),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,
                                contentColor = Color.White
                            ),
                            onClick = {
                                if (state.inAdding) onEventSent(MapContract.Event.AddNote(false))
                                if (state.inEditing) onEventSent(MapContract.Event.SetNewNoteLocation(state.selectedNote.id))
                            }
                        ) {
                            Text(text = "Ок")
                        }
                }
            }
        }

        ModalBottomSheetLayout(
            sheetContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(15.dp))
                        .padding(vertical = 5.dp, horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.TopStart
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = state.selectedNote.noteTitle,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = state.selectedNote.noteText!!,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Box(modifier = Modifier
                        .weight(0.2f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        DescriptionButton(
                            onClicked = {
                                onEventSent(MapContract.Event.ToNoteDescription(state.selectedNote.id))
                            },
                            text = "",
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            image = Icons.Filled.Edit
                        )
                    }
                    Box(modifier = Modifier
                        .weight(0.2f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        DescriptionButton(
                            onClicked = {
                                onEventSent(MapContract.Event.SwitchEditingMode(true))
                            },
                            text = "",
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            image = Icons.Filled.EditLocationAlt
                        )
                    }
                }
                //BottomSheetContent()
            },
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetBackgroundColor = Color.DarkGray,
        ) {
            /*Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = {
                    scope.launch {
                        modalBottomSheetState.show()
                    }
                }) {
                Text(text = "Open Modal Bottom Sheet Layout")
            }*/
        }

        /*LazyColumn(contentPadding = PaddingValues(20.dp)) {
            items(items = state.notes) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(backgroundColor)
                    .clickable { onEventSent(NotesListContract.Event.NotesSelection(it.it)) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "$it", style = MaterialTheme.typography.titleMedium)
                }
            }

        }*/
    //}
//}
    }
}


@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}