package com.example.cahier.data

import androidx.ink.strokes.Stroke
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    /**
     * Retrieve all the notes from the given data source.
     */
    fun getAllNotesStream(): Flow<List<Note>>
    /**
     * Retrieve an note from the given data source that matches with the [id].
     */
    fun getNoteStream(id: Long): Flow<Note>
    /**
     * Insert note in the data source
     */
    suspend fun addNote(note: Note): Long
    /**
     * Delete note from the data source
     */
    suspend fun deleteNote(note: Note)
    /**
     * Update note in the data source
     */
    suspend fun updateNote(note: Note)

    /**
     * Update the strokes data of a note.
     */
    suspend fun updateNoteStrokes(noteId: Long, strokes: List<Stroke>)

    /**
     * Retrieve strokes data for a note.
     */
    suspend fun getNoteStrokes(noteId: Long): List<Stroke>

}