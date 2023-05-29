package com.example.notebookmap.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun CircleButton(
    onClicked: () -> Unit,
    textColor: Color,
    image: ImageVector
) {
    val size = maxOf(
        LocalConfiguration.current.screenWidthDp.dp,
        LocalConfiguration.current.screenHeightDp.dp
    ) / 12

    Button(
        modifier = Modifier.size(size),
        onClick = { onClicked() },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color.DarkGray, shape = CircleShape)
                .fillMaxSize()
                .border(border = BorderStroke(2.dp, Color.Black), shape = CircleShape),  //
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(MaterialTheme.typography.headlineLarge.fontSize.value.dp),
                    imageVector = image,
                    contentDescription = "icon",
                    tint = textColor
                )
            }
        }
    }
}