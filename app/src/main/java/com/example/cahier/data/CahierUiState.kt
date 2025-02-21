package com.example.cahier.data

import androidx.ink.strokes.Stroke

data class CahierUiState(
    val note: Note = Note(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val strokes: List<Stroke> = listOf()
)