package com.example.notebookmap.presentation.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder

@Composable
fun VideoBox(
    onClickedOnVideo: () -> Unit,
    onClickedOnCross: () -> Unit,
    videoUri: String,
) {
    // Загрузчик для превью видео
    val imageLoader = ImageLoader.Builder(context = LocalContext.current)
        .components { add(VideoFrameDecoder.Factory()) }
        .crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(
        model = videoUri,
        imageLoader = imageLoader,
    )

    val imageState = painter.state

    Box(modifier = Modifier.size(100.dp)) {
        Button(
            modifier = Modifier
                .size(100.dp)
                .border(border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)), // в параметр
            onClick = {
                Log.d("video", "video before if show")
                onClickedOnVideo()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            /*if (imageState is AsyncImagePainter.State.Loading) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(color = Color.LightGray)
                        .fillMaxWidth()
                        .height(165.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(30.dp),
                        strokeWidth = 2.dp
                    )
                }
            }*/
            Image(
                painter = painter,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(shape = RectangleShape)
                    .fillMaxSize()
                    .clickable { onClickedOnVideo() }
            )
        }
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "icon",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .size(15.dp) //
                .align(Alignment.TopEnd)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary))
                .clickable(onClick = onClickedOnCross)
        )
    }
}
