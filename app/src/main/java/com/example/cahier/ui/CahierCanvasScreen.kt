package com.example.cahier.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NoteCanvas(
    viewModel: CanvasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isTextFieldVisible by remember { mutableStateOf(false) }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(key1 = Unit) {
            detectTapGestures {
                isTextFieldVisible = true
            }
        }) {
    }
    if (isTextFieldVisible) {
        TextField(
            value = uiState.text,
            onValueChange = { viewModel.updateText(it) },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}