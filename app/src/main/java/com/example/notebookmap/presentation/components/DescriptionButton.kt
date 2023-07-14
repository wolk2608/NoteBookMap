package com.example.notebookmap.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun DescriptionButton( // что за description
    onClicked: () -> Unit,
    text: String = "", // зачем
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    image: ImageVector
) {
    val minSize = maxOf(
        LocalConfiguration.current.screenWidthDp.dp,
        LocalConfiguration.current.screenHeightDp.dp
    ) / 16

    Button(
        modifier = Modifier
            .defaultMinSize(minSize)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)), // вынести в Button.border =
        onClick = onClicked,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(MaterialTheme.typography.headlineLarge.fontSize.value.dp), // typography для текста, а не картинок
                imageVector = image,
                contentDescription = "icon",
                tint = textColor
            )
            Text(text = text) // сюда условие можно
        }
    }
}