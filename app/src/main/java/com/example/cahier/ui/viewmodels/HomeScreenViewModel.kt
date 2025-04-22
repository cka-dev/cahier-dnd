package com.example.cahier.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cahier.data.CahierUiState
import com.example.cahier.data.Note
import com.example.cahier.data.NoteType
import com.example.cahier.data.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val noteRepository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CahierUiState())
    val uiState: StateFlow<CahierUiState> = _uiState.asStateFlow()

    private var newlyAddedId = 0L

    /**
     * Holds ui state for the list of notes on the home pane.
     * The list of items are retrieved from [NoteRepository] and mapped to
     * [NoteListUiState]
     */
    val noteList: StateFlow<NoteListUiState> =
        noteRepository.getAllNotesStream().map { NoteListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = NoteListUiState()
            )

    fun selectNote(noteId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                noteRepository.getNoteStream(noteId)
                    .filterNotNull()
                    .collect { note ->
                        val strokes = if (note.type == NoteType.DRAWING) {
                            noteRepository.getNoteStrokes(noteId)
                        } else {
                            emptyList()
                        }
                        _uiState.value = CahierUiState(note = note, strokes = strokes)
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error retrieving note: ${e.message}",
                    isLoading = false
                )
                Log.e(TAG, "Error retrieving note: ${e.message}")
            }
        }
    }

    fun addNote(callback: (id: Long) -> Unit): Long {
        return addNoteOfType(NoteType.TEXT, callback)
    }

    fun addDrawingNote(callback: (id: Long) -> Unit): Long {
        return addNoteOfType(NoteType.DRAWING, callback)
    }

    private fun addNoteOfType(noteType: NoteType, callback: (id: Long) -> Unit): Long {
        try {
            viewModelScope.launch {
                val newNote = Note(
                    id = 0,
                    title = "",
                    type = noteType,
                    text = if (noteType == NoteType.TEXT) "" else null,
                )
                newlyAddedId = noteRepository.addNote(newNote)
                _uiState.value = CahierUiState(note = newNote.copy(id = newlyAddedId))
                callback(newlyAddedId)
            }
            return newlyAddedId
        } catch (e: Exception) {
            Log.e(TAG, "Error adding note: ${e.message}")
            _uiState.value =
                _uiState.value.copy(error = "Error adding note: ${e.message}")
            return -1
        }
    }

    fun deleteNote() {
        try {
            viewModelScope.launch {
                _uiState.value.note.let {
                    noteRepository.deleteNote(it)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting note: ${e.message}")
            _uiState.value =
                _uiState.value.copy(error = "Error deleting note: ${e.message}")
        }
    }

    fun toggleFavorite(noteId: Long) {
        viewModelScope.launch {
            try {
                noteRepository.toggleFavorite(noteId)
                if (_uiState.value.note.id == noteId) {
                    val updatedNote = noteRepository.getNoteStream(noteId).first()
                    _uiState.update { it.copy(note = updatedNote) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling favorite: ${e.message}")
            }
        }
    }

    fun clearSelection() {
        _uiState.update { CahierUiState() }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        private const val TAG = "HomeScreenViewModel"
    }
}

/**
 * Ui State for HomeScreen
 */
data class NoteListUiState(val noteList: List<Note> = listOf())