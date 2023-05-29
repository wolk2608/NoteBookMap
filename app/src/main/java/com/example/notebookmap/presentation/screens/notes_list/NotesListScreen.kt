package com.example.notebookmap.presentation.screens.notes_list

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun NotesListScreen(
    state: NotesListContract.State,
    effectFlow: Flow<NotesListContract.Effect>?,
    onEventSent: (event: NotesListContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: NotesListContract.Effect.Navigation) -> Unit
) {
    // Listen for side effects from the VM
    LaunchedEffect(Unit) {
        Log.d("LaunchedEffect", "${state}")
        effectFlow?.onEach { effect ->
            when (effect) {
                is NotesListContract.Effect.ToastDataWasLoaded -> { Log.d("Toast", "toast data was loaded") }
                is NotesListContract.Effect.Navigation.ToNoteDescription ->
                    onNavigationRequested(effect)
            }
        }?.collect()
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (state.isLoading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(durationMillis = 500)
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        //Log.d("NotesListLazyColumn", "${state.notes}")
        LazyColumn(
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items = state.notes) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(5.dp)
                        .clickable { onEventSent(NotesListContract.Event.NotesSelection(it.id)) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                        /*Text(
                            text = "id: ${it.id}",
                            style = MaterialTheme.typography.titleLarge
                        )*/
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top) {
                        Text(
                            text = it.noteTitle,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                        Text(
                            text = it.noteText!!,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}