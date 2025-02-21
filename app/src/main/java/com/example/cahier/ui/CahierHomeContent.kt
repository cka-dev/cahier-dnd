package com.example.cahier.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.ink.strokes.Stroke
import com.example.cahier.R
import com.example.cahier.data.Note
import com.example.cahier.data.NoteType

@Composable
fun NoteList(
    noteList: List<Note>,
    onAddNewTextNote: () -> Unit,
    onAddNewDrawingNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteNote: (Note) -> Unit = {},
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Scaffold(
            topBar = {
            }, floatingActionButton = {
                val expanded = remember { mutableStateOf(false) }
                AddFloatingButton(
                    expanded = expanded,
                    onTextNoteSelected = {
                        expanded.value = true
                        onAddNewTextNote()
                    },
                    onDrawingNoteSelected = {
                        expanded.value = true
                        onAddNewDrawingNote()
                    }
                )
            }, modifier = modifier
        ) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(184.dp), Modifier.padding(innerPadding)
            ) {
                items(
                    count = noteList.size,
                    key = { index -> noteList[index].id }
                ) { index ->
                    NoteItem(
                        note = noteList[index],
                        onNoteClick = onNoteClick,
                        onNoteDelete = onDeleteNote
                    )
                }
            }
        }
    }
}

@Composable
fun NoteDetail(
    note: Note,
    strokes: List<Stroke>,
    onClickToEdit: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .clickable { onClickToEdit(note) }
                .padding(16.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            when (note.type) {
                NoteType.TEXT -> {
                    note.text?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                NoteType.DRAWING -> {
                    DrawingPreview(
                        strokes = strokes,
                        onClick = { onClickToEdit(note) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
            note.image?.let { imageRes ->
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = note.title,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    onNoteClick: (Note) -> Unit,
    onNoteDelete: (Note) -> Unit,
    note: Note,
    modifier: Modifier = Modifier
) {
    Surface {
        Column {
            OutlinedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNoteClick(note) }
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painterResource(R.drawable.media),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(184.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete_note),
                            modifier = Modifier.clickable {
                                onNoteDelete(note)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddFloatingButton(
    expanded: MutableState<Boolean>,
    onTextNoteSelected: () -> Unit,
    onDrawingNoteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = expanded.value,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = stringResource(R.string.drawing),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = stringResource(R.string.text_note),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    FloatingActionButton(
                        onClick = {
                            onDrawingNoteSelected()
                            expanded.value = false
                        },
                        modifier = Modifier.padding(bottom = 8.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.stylus_note_24px),
                            contentDescription = stringResource(R.string.drawing_note)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FloatingActionButton(
                        onClick = {
                            onTextNoteSelected()
                            expanded.value = false
                        },
                        modifier = Modifier.padding(top = 8.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.sticky_note_24px),
                            contentDescription = stringResource(R.string.text_note)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            onClick = { expanded.value = !expanded.value },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector =
                    if (expanded.value) Icons.Default.Close else Icons.Default.Add,
                contentDescription = stringResource(R.string.add_note)
            )
        }
    }
}

@Preview
@Composable
fun AddFloatingButtonPreview() {
    val expanded = remember { mutableStateOf(true) }
    AddFloatingButton(
        expanded = expanded,
        onTextNoteSelected = {},
        onDrawingNoteSelected = {}
    )
}