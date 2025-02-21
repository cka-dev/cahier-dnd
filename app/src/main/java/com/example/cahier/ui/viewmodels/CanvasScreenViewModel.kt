package com.example.cahier.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cahier.data.CahierUiState
import com.example.cahier.data.Note
import com.example.cahier.data.NotesRepository
import com.example.cahier.navigation.TextCanvasDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CanvasScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NotesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CahierUiState())
    val uiState: StateFlow<CahierUiState> = _uiState.asStateFlow()

    private val noteId: Long? = savedStateHandle[TextCanvasDestination.NOTE_ID_ARG]

    init {
        viewModelScope.launch {
            if (noteId != null) {
                noteRepository.getNoteStream(noteId)
                    .filterNotNull()
                    .collect {
                        _uiState.value = CahierUiState(note = it)
                    }
            }
        }
    }

    fun updateNoteTitle(title: String) {
        updateNoteField(title) { note, value -> note.copy(title = value) }
    }

    fun updateNoteText(text: String) {
        updateNoteField(text) { note, value -> note.copy(text = value) }
    }

    private fun <T> updateNoteField(value: T, updater: (Note, T) -> Note) {
        try {
            _uiState.value = _uiState.value.copy(
                note = updater(_uiState.value.note, value)
            )
            viewModelScope.launch {
                if (noteId != null)
                    noteRepository.updateNote(_uiState.value.note)
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = "Error updating note: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "CanvasScreenViewModel"
    }
}