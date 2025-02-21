package com.example.cahier.ui

import androidx.annotation.UiThread
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.ink.authoring.InProgressStrokeId
import androidx.ink.authoring.InProgressStrokesFinishedListener
import androidx.ink.authoring.InProgressStrokesView
import androidx.ink.brush.BrushFamily
import androidx.ink.brush.StockBrushes
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke
import androidx.navigation.NavBackStackEntry
import com.example.cahier.R
import com.example.cahier.ui.viewmodels.DrawingCanvasViewModel
import kotlinx.coroutines.launch


@Composable
fun DrawingCanvas(
    navBackStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier,
    drawingCanvasViewModel: DrawingCanvasViewModel = hiltViewModel()
) {
    val uiState by drawingCanvasViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val inProgressStrokesView = remember {
        InProgressStrokesView(context)
    }
    inProgressStrokesView.eagerInit()

    val canvasStrokeRenderer = remember { CanvasStrokeRenderer.create() }
    val coroutineScope = rememberCoroutineScope()


    val listener = remember(inProgressStrokesView) {

        object : InProgressStrokesFinishedListener {
            @UiThread
            override fun onStrokesFinished(strokes: Map<InProgressStrokeId, Stroke>) {
                drawingCanvasViewModel.onStrokesFinished(strokes, inProgressStrokesView)
            }
        }
    }

    drawingCanvasViewModel.setInProgressStrokesFinishedListener(inProgressStrokesView, listener)


    DisposableEffect(Unit) {
        onDispose {
            drawingCanvasViewModel.removeInProgressStrokesFinishedListener(
                inProgressStrokesView,
                listener
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        TextField(
            value = uiState.note.title,
            onValueChange = { newTitle ->
                coroutineScope.launch {
                    drawingCanvasViewModel.updateNoteTitle(newTitle)
                }
            },
            placeholder = { Text(text = stringResource(R.string.drawing_title)) },
            modifier = Modifier.fillMaxWidth()
        )
        DrawingToolbox(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            drawingCanvasViewModel = drawingCanvasViewModel
        )
        DrawingSurface(
            strokes = uiState.strokes,
            inProgressStrokesView = inProgressStrokesView,
            canvasStrokeRenderer = canvasStrokeRenderer,
            onDrawing = drawingCanvasViewModel::handleDrawing,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun DrawingToolbox(
    modifier: Modifier = Modifier,
    drawingCanvasViewModel: DrawingCanvasViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var brushMenuExpanded by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.background(
            MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        )
    ) {
        IconButton(onClick = {
            brushMenuExpanded = true
            drawingCanvasViewModel.setEraserMode(false)
        }) {
            Icon(
                painter = painterResource(R.drawable.brush_24px),
                contentDescription = stringResource(R.string.brush),
            )
        }

        BrushDropdownMenu(
            expanded = brushMenuExpanded,
            onDismissRequest = { brushMenuExpanded = false },
            onBrushChange = { newBrush ->
                coroutineScope.launch {
                    drawingCanvasViewModel.changeBrush(newBrush)
                }
                brushMenuExpanded = false
            }
        )

        IconButton(onClick = {
            showColorPicker = true
            drawingCanvasViewModel.setEraserMode(false)
        }) {
            Icon(
                painter = painterResource(R.drawable.palette_24px),
                contentDescription = stringResource(R.string.color),
            )
        }

        ColorPickerDialog(
            showDialog = showColorPicker,
            onDismissRequest = { showColorPicker = false },
            onColorSelected = { color ->
                coroutineScope.launch {
                    drawingCanvasViewModel.changeBrushColor(color)
                }
                showColorPicker = false
            }
        )

        IconButton(onClick = { drawingCanvasViewModel.setEraserMode(true) }) {
            Icon(
                painter = painterResource(R.drawable.ink_eraser_24px),
                contentDescription = stringResource(R.string.eraser)
            )
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    drawingCanvasViewModel.clearStrokes()
                }
            }
        ) {
            Text(text = stringResource(R.string.clear))
        }

        Spacer(modifier = Modifier.size(4.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    drawingCanvasViewModel.saveStrokes()
                }
            }) {
            Text(text = stringResource(R.string.save))
        }
    }
}


@Composable
fun BrushDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onBrushChange: (BrushFamily) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true)
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.pressure_pen)) },
            onClick = { onBrushChange(StockBrushes.pressurePenLatest) }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.marker)) },
            onClick = { onBrushChange(StockBrushes.markerLatest) }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.highlighter)) },
            onClick = { onBrushChange(StockBrushes.highlighterLatest) }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.dashed_line)) },
            onClick = { onBrushChange(StockBrushes.dashedLineLatest) }
        )
    }
}