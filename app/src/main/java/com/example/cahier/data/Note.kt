package com.example.cahier.data

import java.time.LocalDate


data class Note(
    val id: Long,
    val title: String,
    val date: LocalDate
)