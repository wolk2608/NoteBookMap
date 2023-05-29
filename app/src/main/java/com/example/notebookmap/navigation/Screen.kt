package com.example.notebookmap.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Map
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.notebookmap.utils.Constants.DESCRIPTION_ARGUMENT

sealed class Screen (
    val route : String,
    val title: String? = null,
    val icon: ImageVector? = null
    ) {

    object NotesList : Screen("notesList", "List", Icons.Outlined.List)
    object Map : Screen("map", "Map", Icons.Outlined.Map)
    object NoteDescription : Screen("noteDescription/{$DESCRIPTION_ARGUMENT}") { fun passParam(noteId: Long) = "noteDescription/$noteId" }
}
