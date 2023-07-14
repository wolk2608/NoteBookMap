package com.example.notebookmap.presentation.components

import android.media.MediaPlayer
import android.provider.OpenableColumns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

// Creating a composable function to
// create two icon buttons namely play and pause
// Calling this function as content in the above function
@Composable
fun AudioBox(
    onClickedOnCross: () -> Unit,
    audioUriString: String
) {
    val context = LocalContext.current
    val audioUri = audioUriString.toUri()
    var audioName: String = ""

    // Declaring and Initializing
    // the MediaPlayer to play audio
    val mediaPlayer = remember { MediaPlayer.create(context, audioUri) }
    val icon = remember { mutableStateOf(Icons.Filled.PlayArrow) }
    var isPlaying by remember { mutableStateOf(false) } // должно быть в VM

    val minSize = maxOf(
        LocalConfiguration.current.screenWidthDp.dp,
        LocalConfiguration.current.screenHeightDp.dp
    ) / 16 // заменить

    // Получаем название аудио
    if (audioUri.scheme == "content") {
        val cursor = context.contentResolver.query(audioUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    audioName = it.getString(displayNameIndex)
                }
            }
        }
    }

    // Воспроизведение или пауза аудио
    val togglePlayback: () -> Unit = {
        if (isPlaying) {
            mediaPlayer.pause()
            icon.value = Icons.Filled.PlayArrow
        } else {
            mediaPlayer.start()
            icon.value = Icons.Filled.Pause
        }
        isPlaying = !isPlaying
    }

    // Обработка окончания проигрывания аудио
    mediaPlayer.setOnCompletionListener {
        isPlaying = false
        icon.value = Icons.Filled.PlayArrow
    }

    // Остановка воспроизведения при уничтожении компонента
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(minSize)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // IconButton for audio Action
            IconButton(onClick = { togglePlayback() }) { // можно сделать private функцию
                Icon(
                    imageVector = icon.value,
                    contentDescription = "icon",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(MaterialTheme.typography.headlineLarge.fontSize.value.dp) // typography для текста
                        .defaultMinSize(minSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = CircleShape
                        ),
                )
            }
            IconButton(onClick = { onClickedOnCross() }) { // копипаст
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "icon",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(MaterialTheme.typography.headlineLarge.fontSize.value.dp)
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
            Text(
                text = audioName,
                color = MaterialTheme.colorScheme.onPrimary // почему onPrimary? нигде нет background = primary
            )
        }
    }
}