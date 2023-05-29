package com.example.notebookmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.notebookmap.navigation.SetupNavGraph
import com.example.notebookmap.ui.theme.NoteBookMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteBookMapTheme {
                SetupNavGraph()
            }
        }
    }
}