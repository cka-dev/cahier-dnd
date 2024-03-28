package com.example.cahier.data

data class HomePaneUiState(
    val note: Note? = null,
    val notes: List<Note> = LocalNotesDataProvider.allNotes,
    val notesCount: Int = LocalNotesDataProvider.allNotes.size
)