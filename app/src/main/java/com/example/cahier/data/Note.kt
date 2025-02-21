package com.example.cahier.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "text")
    val text: String? = null,
    @ColumnInfo(name = "image")
    val image: Int? = null,
    @ColumnInfo(name = "type")
    val type: NoteType = NoteType.TEXT,
    @ColumnInfo(name = "strokes_data")
    val strokesData: String? = null
) : Parcelable

enum class NoteType {
    TEXT,
    DRAWING
}