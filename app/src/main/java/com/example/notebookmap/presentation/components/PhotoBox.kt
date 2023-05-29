package com.example.notebookmap.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage

@Composable
fun PhotoBox(
    onClickedOnPhoto: () -> Unit,
    onClickedOnCross: () -> Unit,
    photoUri: String,
) {
    val showDialog = remember { mutableStateOf(false) }

    Box(modifier = Modifier.size(100.dp)) {
        Button(
            modifier = Modifier
                .size(100.dp)
                .border(border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)),
            onClick = {
                showDialog.value = true
                onClickedOnPhoto()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Note photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RectangleShape)
            )
        }
        Icon(
            modifier = Modifier
                .size(15.dp)
                .align(Alignment.TopEnd)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary))
                .clickable(onClick = onClickedOnCross),
            imageVector = Icons.Filled.Close,
            contentDescription = "icon",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
    if (showDialog.value) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showDialog.value = false }) {
            Box(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(15.dp),
                contentAlignment = Alignment.Center
            ) {
                ZoomableAsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .border(border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Note photo"
                )
                //}
            }
        }
    }
}
