package com.example.cahier.ui

import androidx.lifecycle.ViewModel
import com.example.cahier.data.HomePaneUiState
import com.example.cahier.data.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomePaneViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomePaneUiState())
    val uiState: StateFlow<HomePaneUiState> = _uiState.asStateFlow()

    fun updateNote(note: Note?) {
        _uiState.update { currentState ->
            currentState.copy(note = note)
        }
    }
}