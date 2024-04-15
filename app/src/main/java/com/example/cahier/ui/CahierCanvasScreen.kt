package com.example.cahier.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.android.awaitFrame

@Composable
fun NoteCanvas(
    viewModel: CanvasViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var isTextFieldVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(key1 = Unit) {
                detectTapGestures {
                    isTextFieldVisible = true
                }
            }
    ) {
    }
    if (isTextFieldVisible) {
        TextField(
            value = uiState.text,
            onValueChange = { viewModel.updateText(it) },
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                shouldShowKeyboardOnFocus = true
            ),
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
        )
        // Ensures that the keyboard is immediately shown when the TextField is first drawn on the screen
        LaunchedEffect(focusRequester) {
            awaitFrame()
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun CanvasWrapper(modifier: Modifier = Modifier) {
    Scaffold() {
        NoteCanvas(modifier = Modifier.padding(it))
    }
}