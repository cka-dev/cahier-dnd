package com.example.cahierreview.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cahierreview.data.Note
import com.example.myapplication.R
import java.time.format.DateTimeFormatter

@Composable
fun HomePane(
    notes: List<Note>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            LargeFloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Filled.Add,
                    stringResource(R.string.floating_action_button_des)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(184.dp),
            modifier.padding(innerPadding)
        )
        {
            items(notes.size) { note ->
                NoteItem(note = notes[note])
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier
) {
    val date = note.date
    val formatter = DateTimeFormatter.ofPattern(stringResource(R.string.date_pattern))
    val formattedDate = date.format(formatter)
    OutlinedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(6.dp)
    ) {
        Image(
            painterResource(R.drawable.media),
            contentDescription = null,
            modifier = Modifier.heightIn(115.dp)
        )
        Column(modifier.padding(16.dp)) {
            Text(note.title)
            Text(formattedDate.toString())
        }
    }
}