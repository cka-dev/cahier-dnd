package com.example.cahier.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.cahier.R

@Composable
fun ColorPickerDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismissRequest) {
            ColorPickerContent(
                onColorSelected = onColorSelected,
                onDismissRequest = onDismissRequest
            )
        }
    }
}

@Composable
private fun ColorPickerContent(onColorSelected: (Color) -> Unit, onDismissRequest: () -> Unit) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.select_color),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            ColorGrid(onColorSelected = onColorSelected)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDismissRequest, modifier = Modifier.align(Alignment.End)) {
                Text(text = stringResource(R.string.dismiss))
            }
        }
    }
}


@Composable
private fun ColorGrid(onColorSelected: (Color) -> Unit) {
    val colors = remember {
        listOf(
            Color.Black, Color.Gray, Color.White, Color.Red, Color.Green, Color.Blue,
            Color.Yellow, Color.Cyan, Color.Magenta, Color.DarkGray, Color.LightGray
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 40.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(colors.size) { index ->
            ColorSwatch(color = colors[index]) { selectedColor ->
                onColorSelected(selectedColor)
            }
        }
    }
}

@Composable
private fun ColorSwatch(color: Color, onColorSelected: (Color) -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onColorSelected(color) }
    )
}