package com.example.cahier.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cahier.R

@Composable
fun HomePane(
    modifier: Modifier = Modifier,
    viewModel: HomePaneViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            CahierTopAppBar()
        },
        floatingActionButton = {
            LargeFloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Add, stringResource(R.string.floating_action_button_des))
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(184.dp),
            Modifier.padding(innerPadding)
        )
        {
            items(uiState.notesCount) { note ->
                NoteItem(note = uiState.notes[note])
            }
        }
    }
}