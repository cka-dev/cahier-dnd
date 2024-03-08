package com.example.cahierreview.data

import java.time.LocalDate


data class Note(
    val id: Long,
    val title: String,
    val date: LocalDate
)