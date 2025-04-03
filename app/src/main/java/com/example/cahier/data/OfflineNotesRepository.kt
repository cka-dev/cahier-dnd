package com.example.cahier.data

import androidx.ink.strokes.Stroke
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

class OfflineNotesRepository(private val notesDao: NoteDao) : NotesRepository {

    private val converters = Converters()

    override fun getAllNotesStream(): Flow<List<Note>> = notesDao.getAllNotes()

    override fun getNoteStream(id: Long): Flow<Note> = notesDao.getNote(id)

    override suspend fun addNote(note: Note): Long {
        return notesDao.addNote(note)
    }

    override suspend fun deleteNote(note: Note) = notesDao.deleteNote(note)

    override suspend fun updateNote(note: Note) = notesDao.updateNote(note)

    override suspend fun updateNoteStrokes(noteId: Long, strokes: List<Stroke>) {
        val strokesData = strokes.map { converters.serializeStroke(it) }
        val strokesJson = Gson().toJson(strokesData)

        val note = notesDao.getNoteById(noteId)
        if (note != null) {
            val updatedNote = note.copy(strokesData = strokesJson)
            notesDao.updateNote(updatedNote)
        }
    }

    override suspend fun getNoteStrokes(noteId: Long): List<Stroke> {
        val note = notesDao.getNoteById(noteId)
        val strokesJson = note?.strokesData ?: return emptyList()

        val strokesData: List<String> =
            Gson().fromJson(strokesJson, object : TypeToken<List<String>>() {}.type)
        return strokesData.mapNotNull { converters.deserializeStrokeFromString(it) }
    }

    override suspend fun toggleFavorite(noteId: Long) {
        val note = notesDao.getNoteById(noteId)
        if (note != null) {
            val updatedNote = note.copy(isFavorite = !note.isFavorite)
            notesDao.updateNote(updatedNote)
        }
    }

    override suspend fun updateNoteImageUriList(noteId: Long, imageUriList: List<String>?) {
        val note = notesDao.getNoteById(noteId)
        if (note != null) {
            val updatedNote = note.copy(imageUriList = imageUriList)
            notesDao.updateNote(updatedNote)
        }
    }

}