package com.example.cahier.ui.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cahier.data.CahierUiState
import com.example.cahier.data.Note
import com.example.cahier.data.NotesRepository
import com.example.cahier.ui.NoteCanvasDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CanvasScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NotesRepository
) : ViewModel() {

    private val _note = MutableStateFlow(CahierUiState())
    val note: StateFlow<CahierUiState> = _note.asStateFlow()

    private val noteId: Long? = savedStateHandle[NoteCanvasDestination.NOTE_ID_ARG]

    init {
        viewModelScope.launch {
            noteRepository.getNoteStream(noteId!!)
                .filterNotNull()
                .collect {
                    _note.value = CahierUiState(it)
                }
        }
    }

    fun updateNoteTitle(title: String) {
        try {
            updateUiState(note.value.note.copy(title = title))
            viewModelScope.launch {
                noteRepository.updateNote(note.value.note.copy(title = title))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating note: ${e.message}")
        }
    }

    fun updateNoteText(text: String) {
        try {
            updateUiState(note.value.note.copy(text = text))
            viewModelScope.launch {
                noteRepository.updateNote(note.value.note.copy(text = text))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating note: ${e.message}")
        }
    }

    private fun updateUiState(note: Note) {
        try {
            _note.update { it.copy(note = note) }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating note: ${e.message}")
        }
    }


    companion object {
        private const val TAG = "CanvasScreenViewModel"
    }
}