package com.example.cahier.di

import android.content.Context
import com.example.cahier.data.NoteDatabase
import com.example.cahier.data.NotesRepository
import com.example.cahier.data.OfflineNotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase {
        return NoteDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(database: NoteDatabase): NotesRepository {
        return OfflineNotesRepository(database.noteDao())
    }
}