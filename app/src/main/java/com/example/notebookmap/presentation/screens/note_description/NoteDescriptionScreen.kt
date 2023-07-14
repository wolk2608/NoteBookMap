package com.example.notebookmap.presentation.screens.note_description

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.example.notebookmap.presentation.components.AudioBox
import com.example.notebookmap.presentation.components.DescriptionButton
import com.example.notebookmap.presentation.components.PhotoBox
import com.example.notebookmap.presentation.components.VideoBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat", "WeekBasedYear")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDescriptionScreen(
    state: NoteDescriptionContract.State,
    effectFlow: Flow<NoteDescriptionContract.Effect>?,
    onEventSent: (event: NoteDescriptionContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: NoteDescriptionContract.Effect.Navigation) -> Unit,
) {
    // Listen for side effects from the VM
    LaunchedEffect(Unit) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is NoteDescriptionContract.Effect.ToastDataWasLoaded -> Log.d("Toast", "toast data was loaded")
                is NoteDescriptionContract.Effect.Navigation.ToMap -> onNavigationRequested(effect)
                is NoteDescriptionContract.Effect.Navigation.PopBackStack -> onNavigationRequested(effect)
            }
        }?.collect()
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { onEventSent(NoteDescriptionContract.Event.AddPhotos(it)) }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { onEventSent(NoteDescriptionContract.Event.AddVideos(it)) }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { onEventSent(NoteDescriptionContract.Event.AddAudios(it)) }


    /*val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { sharedViewModel.setSharedImage(it) }*/

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { selectedUri ->
        if (selectedUri != null) {
            println("File selected = $selectedUri")
        } else {
            println("No file was selected")
        }
    }

    val minSize = maxOf( // задать размер нормально
        LocalConfiguration.current.screenWidthDp.dp,
        LocalConfiguration.current.screenHeightDp.dp
    )

    val context = LocalContext.current

    // Посмотреть можно ли удалить Box
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // такого быть не должно
    ) {
        // Лучше Column
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            /*items(items = state.notes) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(backgroundColor)
                        .clickable { onEventSent(NotesListContract.Event.NotesSelection(it.id)) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "$it", style = MaterialTheme.typography.titleMedium)
                }
            }*/
            item { // Весь item это topBar Scaffold
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // посмотреть зачем
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DescriptionButton(
                        onClicked = { onEventSent(NoteDescriptionContract.Event.PopBackStack) },
                        image = Icons.Filled.ArrowBack
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        DescriptionButton(
                            onClicked = {
                                val intent = Intent()
                                intent.action = Intent.ACTION_SEND_MULTIPLE
                                intent.putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${state.newNoteTitle}\n" +
                                            "${state.newNoteText}\n" +
                                            "Найти на карте: \n" +
                                            "https://yandex.ru/maps/?ll=${state.note.noteLongitude}%2C${state.note.noteLatitude}&mode=whatshere&whatshere%5Bpoint%5D=${state.note.noteLongitude}%2C${state.note.noteLatitude}&whatshere%5Bzoom%5D=15&z=15"
                                )
                                intent.type = "text/plain"

                                val files: MutableList<Uri> = mutableListOf()
                                state.newPhotosUri.forEach { files.add(it.toUri()) }
                                state.newVideosUri.forEach { files.add(it.toUri()) }
                                state.newAudiosUri.forEach { files.add(it.toUri()) }
                                if (files.isNotEmpty()) {
                                    intent.putParcelableArrayListExtra(
                                        Intent.EXTRA_STREAM,
                                        ArrayList(files)
                                    )
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                startActivity(
                                    context,
                                    Intent.createChooser(intent, "Выберете приложение"),
                                    null
                                )
                            },
                            image = Icons.Filled.Share
                        )
                        DescriptionButton(
                            onClicked = {
                                onEventSent(NoteDescriptionContract.Event.SaveNote)
                                Log.d(
                                    "DescriptionScreen save video",
                                    "${state.newVideosUri.toList()}"
                                )
                            },
                            image = Icons.Filled.Save
                        )
                        DescriptionButton(
                            onClicked = { onEventSent(NoteDescriptionContract.Event.DeleteNote) },
                            image = Icons.Filled.Delete
                        )
                    }
                }
            }
            item {
                Text(
                    text = "Название",
                    color = MaterialTheme.colorScheme.onBackground, // этого быть не должно тут
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(3.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colorScheme.primaryContainer) // ???
                        .clickable { },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .border(
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onPrimary
                                )
                            ),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        value = state.newNoteTitle,
                        onValueChange = { onEventSent(NoteDescriptionContract.Event.ChangeNoteTitle(it)) }
                    )
                }
            }
            item {
                Text(
                    text = "Описание",
                    color = MaterialTheme.colorScheme.onBackground, //
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(10.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // попробовать убрать
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    value = state.newNoteText,
                    onValueChange = { onEventSent(NoteDescriptionContract.Event.ChangeNoteText(it)) }
                )
            }
            item {
                Text(
                    text = "Фото",
                    color = MaterialTheme.colorScheme.onBackground, //
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(10.dp))
                if (state.newPhotosUri.isNotEmpty()) {
                    LazyRow(
                        // horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        items(items = state.newPhotosUri) {
                            PhotoBox(
                                onClickedOnPhoto = {
                                    // картинка разворачивается на весь экран уже через PhotoBox
                                },
                                onClickedOnCross = {
                                    onEventSent(NoteDescriptionContract.Event.DeletePhoto(it))
                                },
                                photoUri = it
                            )
                            Spacer(modifier = Modifier.size(3.dp)) // ?
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
                Row(
                    modifier = Modifier
                        .wrapContentSize() // попробовать удалить
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    DescriptionButton(
                        onClicked = {
                            photoPicker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        image = Icons.Filled.Add
                    )
                }
            }
            item {
                Text(
                    text = "Видео",
                    color = MaterialTheme.colorScheme.onBackground, //
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(10.dp))
                if (state.newVideosUri.isNotEmpty()) {
                    LazyRow {
                        items(items = state.newVideosUri) {
                            VideoBox(
                                onClickedOnVideo = {
                                    // Подготовка для открытия видео в другом приложении
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(it.toUri(), "video/*")
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                                    val packageManager = context.packageManager
                                    // предложить открыть видео в другом приложении (плеере)
                                    if (intent.resolveActivity(packageManager) != null) {
                                        context.startActivity(intent)
                                        Log.d("video", "video shown")
                                    } else {
                                        Toast.makeText( // заменить на snackbar
                                            context,
                                            "Нет подходящего приложения для просмотра видео",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("video", "video not shown")
                                    }
                                },
                                onClickedOnCross = {
                                    onEventSent(NoteDescriptionContract.Event.DeleteVideo(it))
                                },
                                videoUri = it
                            )
                            Spacer(modifier = Modifier.size(3.dp)) // в horizontal вынести
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
                Row(
                    modifier = Modifier
                        .wrapContentSize() // удалить?
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    DescriptionButton(
                        onClicked = {
                            videoPicker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.VideoOnly
                                )
                            )
                        },
                        image = Icons.Filled.Add
                    )
                }
            }
            item {
                Text(
                    text = "Аудио",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(10.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize() // ?????
                        .height(minSize / 2) // ?
                        .border(
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        .background(MaterialTheme.colorScheme.background) // не должно указываться
                        .align(Alignment.TopStart) // в vertical / horizontal arragment вынести!
                ) {
                    if (state.newAudiosUri.isNotEmpty()) {
                        items(items = state.newAudiosUri) {
                            AudioBox(
                                // обработка обычного нажатия на аудио происходит уже внутри реализации AudioBox
                                onClickedOnCross = { onEventSent(NoteDescriptionContract.Event.DeleteAudio(it)) },
                                audioUriString = it
                            )
                        }
                    }
                    item {
                        IconButton(onClick = { audioPicker.launch(arrayOf("audio/*")) }) { // копипаст из AudioBox
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add music icon",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .size(MaterialTheme.typography.headlineLarge.fontSize.value.dp) //
                                    .defaultMinSize(minSize)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .border(
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        ), shape = CircleShape
                                    ),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
            item {
                Text(
                    text = "Координаты:",
                    color = MaterialTheme.colorScheme.onBackground, //
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(5.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // ??
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Широта: ${state.note.noteLatitude}",
                            color = MaterialTheme.colorScheme.onBackground //
                        )
                        Text(
                            text = "Долгота: ${state.note.noteLongitude}",
                            color = MaterialTheme.colorScheme.onBackground //
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        /*DescriptionButton(
                            onClicked = { /*TODO*/ },
                            text = "",
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            image = Icons.Filled.Edit
                        )*/
                        DescriptionButton(
                            onClicked = { onEventSent(NoteDescriptionContract.Event.OpenNoteInMap) },
                            image = Icons.Outlined.Map
                        )
                    }
                }
            }
            item {
                Text(
                    text = "Дата создания заметки: ${SimpleDateFormat("dd.MM.yyyy").format(state.note.noteDate)}", // это должно быть в VM
                    color = MaterialTheme.colorScheme.onBackground // ??
                )
                Text(
                    text = "Время создания заметки: ${SimpleDateFormat("HH:mm:ss").format(state.note.noteTime)}",
                    color = MaterialTheme.colorScheme.onBackground // ??
                )
            }
        }
    }
}